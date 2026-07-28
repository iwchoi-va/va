# -*- coding: utf-8 -*-
"""
    (C) Copyright Hansol Inticube co, Ltd 2017
        Writer : Jin Kak, Jung
            Revision History :
            First released on Apr, 30, 2017
"""
import datetime

from cython.parallel import parallel, prange

cimport cython
cimport openmp
import os
import numpy as np

from ds import document_similarity
# from keyword_extraction import keyword_extraction
from dataproc import mssqldb, extractFile
import pymssql

import weakref
from utils import log, utils
from proc_base import proc_base

class ds_proc(proc_base):

    def __init__(self, _num_threads, _log_filename, _parent,
            _mssqldb_host="localhost", _mssqldb_host_back="localhost", _mssqldb_port=1443, _mssqldb_db="msens_ob", _mssqldb_user="bXNlbnM=", _mssqldb_pwd="bXNlbnMh"):

        proc_base.__init__(self, _num_threads, _log_filename, _parent)

        self.rdb = {"host":_mssqldb_host, "host_back":_mssqldb_host_back, "port":_mssqldb_port, "database":_mssqldb_db, "user":_mssqldb_user, "pwd":_mssqldb_pwd}
        self.mssqldb = mssqldb(_host=self.rdb["host"], _host_back=self.rdb["host_back"], _port=self.rdb["port"], _dbname=self.rdb["database"], _user=self.rdb["user"], _pwd=self.rdb["pwd"])

        self.ds_options = property(fget=weakref.ref(self.get_ds_options), fset=weakref.ref(self.set_ds_options))

        self.ds = document_similarity(_num_threads) #문장유사도 돌리는 부분인거 같음
        self.ds.document_frequency = (1, 1.0)

        self.logger.write_log("info", "[[ds_proc]] %s  DS processing object initialized" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')))

        #self.do_vectorizing()


    def __del__(self):
        proc_base.__del__(self)
        self.logger.write_log("info", "[[ds_proc]] DS processing object deinitialized")
        del self.ds


    def get_ds_options(self):
        try:
            return (self.result_size, self.threshold)
        except AttributeError as e:
            print(e)


    def set_ds_options(self, _result_size, _threshold):
        self.result_size = _result_size
        self.threshold = _threshold


    # Select msSQLDB rule data and put it into vectorizing method as a parameter
    def do_vectorizing(self):
        try:
            self.mssqldb = mssqldb(_host=self.rdb["host"], _port=self.rdb["port"], _dbname=self.rdb["database"], _user=self.rdb["user"], _pwd=self.rdb["pwd"])

            self.rule_scripts_ALL = self.mssqldb.read_rule_script_ALL()
        except Exception, err:
            print("DB error retrying retrieve session")
            print(err[0])

        if not self.rule_scripts_ALL:
            self.logger.write_log("info", "[[ds_proc::rule_scripts_ALL]] No Data form DB!")
            raise Exception('No Rule Script!')
        else:
            try:
                self.ds.vectorizing(self.rule_scripts_ALL)
            except Exception, e:
                msg = "exception occurred while training data set => {0}".format(e)
                self.logger.write_log("error", msg)
                raise e

            self.logger.write_log("info", "[[ds_proc]] %s  script vectorizing success" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')))


    def __run_extraction(self, _thread_num, _fileid, _conid, _rxtx_gb, _sentences, _sent_tx, _start_time):

        self.logger.write_log("info", "[[ds_proc]] %s  start __run_extraction, thread id => %d" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'), _thread_num))

        resultset = []
        resultset_append = resultset.append
        _isQstKeyword = "N"
        for idx, sent in enumerate(_sentences): # _sentences: content, sent: a sentence

            # No need to be analized under 3 characters
            # RXTX_GB=Y(RX:상담원/TX:고객), RXTX_GB=N(TX:상담원/고객)
            if _rxtx_gb == "Y":
                _skip_gb = "TX"
            else :
                _skip_gb = "RX"
            if len(sent) < 10 or _sent_tx[idx]==_skip_gb:
                continue

            try:
                (_r_size, _r_threshold) = self.ds_options
            except TypeError as e:
                raise TypeError('Set document similarity factor first' + e.args[0])

            result = []
            result_append = result.append
            _STT_SENT_ID = idx+1
            try:
                for _SC_SNO, _SC_LCLF_CD, _SC_SCLF_CD, _QST_CD, _CNTR_CD, _CTRA_INSA_CLCD, _QST_KEYWORD, _SCRIPT_SENT_ID, cos_sim, result_sentence \
                            in self.ds.get_similar_sentences(_thread_num, _fileid, sent, _r_size, _r_threshold):
                    if _QST_KEYWORD is not None:
                        _isQstKeyword = "Y"
                    result_append((_conid, _fileid, _SC_SNO, _SC_LCLF_CD, _SC_SCLF_CD, _QST_CD, _CNTR_CD, _CTRA_INSA_CLCD, \
                                    _SCRIPT_SENT_ID, _STT_SENT_ID, _start_time[idx], cos_sim, result_sentence))
            except Exception, e:
                msg = "exception occurred while processing sentence simiarity => {0}".format(e)
                self.logger.write_log("error", msg)
                pass

            if len(result) > 0:
                resultset_append((sent, result, _isQstKeyword))

            del result

        return resultset


    def do_processing(self):

        # 1. 콜 CONTENT 수집
        doc_ids=[]
        doc_content=[]
        ids_append = doc_ids.append
        content_append = doc_content.append

        try:
            read_contents = self.mssqldb.read_contents() #대상을 찾는 부분
            if not read_contents:
                self.logger.write_log("info", "[[ds_proc::read_contents]] No Data form DB!")
                pass
            else:
                for row in read_contents:
                    UCID, CON_ENT_DGN_NO, CONTENT, RXTX_GB=row

                    #doc_ids.append({"UCID":UCID, "CON_ENT_DGN_NO":CON_ENT_DGN_NO, "RXTX_GB":RXTX_GB})
                    #doc_content.append(CONTENT)

                    ids_append({"UCID":UCID, "CON_ENT_DGN_NO":CON_ENT_DGN_NO, "RXTX_GB":RXTX_GB})
                    content_append(CONTENT)
                    self.mssqldb.update_tag_flag(UCID, CON_ENT_DGN_NO, "S")
        except Exception, e:
            #raise e
            print("[[ds_proc]] DB Connectionn Fail!")
            return

        cdef int i = 0
        cdef int thread_num = 0
        cdef int num_threads_ = self.num_threads

        sents_full = np.empty((self.num_threads,), dtype=object)
        sents_tx = np.empty((self.num_threads,), dtype=object)
        sents_rx = np.empty((self.num_threads,), dtype=object)
        sents_full_pos = np.empty((self.num_threads,), dtype=list)
        sents_full_tx = np.empty((self.num_threads,), dtype=list)
        textid = np.empty((self.num_threads,), dtype=object)
        conid = np.empty((self.num_threads,), dtype=object)
        rxtx_gb = np.empty((self.num_threads,), dtype=object)
        script_map = np.empty((self.num_threads,), dtype=object)
        resultset_ds = np.empty((self.num_threads,), dtype=object)
        n = len(read_contents)

        with nogil,parallel(num_threads=num_threads_):
            for i in prange(n, schedule='dynamic'):
                thread_num = openmp.omp_get_thread_num()
                with gil:
                    self.logger.write_log("info", "[[ds_proc]] %s  analyze document %s, thread id => %d" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'), doc_ids[i], thread_num))
                    textid[thread_num] = doc_ids[i]["UCID"]
                    conid[thread_num] = doc_ids[i]["CON_ENT_DGN_NO"]
                    rxtx_gb[thread_num] = doc_ids[i]["RXTX_GB"]

                    # 2. 수집 완료 플래그 처리
                    # self.mssqldb.update_tag_flag(textid[thread_num], conid[thread_num], "S")


                    # 3. 콜별 스크립트 정보 수집
                    try:
                        script_map[thread_num] = self.mssqldb.read_script_map(textid[thread_num], conid[thread_num])
                    except Exception, e:
                        print(e)

                    if not script_map[thread_num]:
                        self.logger.write_log("info", "[[ds_proc::read_script_map]] No Data form DB!, thread id => %d" % (thread_num))
                        self.mssqldb.update_tag_flag(textid[thread_num], conid[thread_num], "E")
                        continue
                    else:
                        try:
                            self.ds.vectorizing(thread_num, script_map[thread_num])
                        except Exception, e:
                            msg = "exception occurred while training data set => {0}".format(e)
                            self.logger.write_log("error", msg)
                            self.mssqldb.update_tag_flag(textid[thread_num], conid[thread_num], "N")
                            raise e

                        self.logger.write_log("info", "[[ds_proc]] %s  script vectorizing success, thread id => %d" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'),thread_num))


                    # 4. Contents 파싱작업
                    sents_full[thread_num], sents_full_pos[thread_num], sents_full_tx[thread_num] = self._read_db_contents(doc_content[i])

                    if sents_full[thread_num] :
                        try:
                            # 4. 유사도 돌리러 고고
                            resultset_ds[thread_num] = self.__run_extraction(thread_num, textid[thread_num], conid[thread_num], rxtx_gb[thread_num], sents_full[thread_num], sents_full_tx[thread_num], sents_full_pos[thread_num])

                            self.logger.write_log("info", "[[ds_proc]] %s  complete resultset_ds %s, thread id => %d" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'),doc_ids[i], thread_num))

                            # 5. 결과 입력
                            if resultset_ds[thread_num]:
                                self.mssqldb.register_sent_similarity(textid[thread_num], resultset_ds[thread_num])

                            # 6. 분석 완료 플래그 처리
                            _qst_keyword = [row[2] for row in resultset_ds[thread_num]]
                            if  _qst_keyword.count("Y") > 0:
                                self.mssqldb.update_tag_flag(textid[thread_num], conid[thread_num], "T") #완료 flag(필수 키워드 있으면 T)
                            else:
                                self.mssqldb.update_tag_flag(textid[thread_num], conid[thread_num], "Y") #완료 flag(필수 키워드 없으면 Y)


                        except Exception, e:
                            msg = "File => %s, exception(DOCUMENT SIMILARITY) => %s" % (textid[thread_num], e.args[0])
                            self.logger.write_log("warning", msg)
                            self.mssqldb.update_tag_flag(textid[thread_num], conid[thread_num], "N")
                            continue


        del doc_ids
        del doc_content
        del read_contents
        del script_map
        del resultset_ds
        del textid
        del conid
        del sents_full
        del sents_full_tx
        del sents_full_pos
