# -*- coding: utf-8 -*-
"""
    (C) Copyright Hansol Inticube co, Ltd 2017
        Writer : Jin Kak, Jung
            Revision History :
            First released on Apr, 30, 2017
"""
from abc import ABCMeta, abstractmethod
import logging
import os
import weakref
from utils import log, utils

import pandas as pd
from StringIO import StringIO

class proc_base(object):

    __metaclass__=ABCMeta

    def __init__(self, _num_threads, _log_filename, _parent):
        self.num_threads = _num_threads
        self.parent = weakref.ref(_parent)
        self.utils = utils()

        self.logger = _parent.logger
        self.logger.write_log("info", "[[proc_base]] Base processing object initialized")

    def __del__(self):
        del self.utils
        self.logger.write_log("info", "[[proc_base]] Base processing object deinitialized")

    def _read_stopwords(self):
        try:
            f = open("./stopwords.txt", "r")
        except IOError as e:
            print("error", e)
            f.close()
            return []

        try:
            stopwords = f.read()
            stopwords = [s.strip() for s in stopwords.replace("\n","").split(",")]
        except Exception, e:
            print("error %s" % e.args)
            raise e
        finally:
            f.close()

        print '_read_stopwords'
        return stopwords

    def _get_textid_from_filename(self, _filefullpathname):
        if isinstance(_filefullpathname, str) == False:
            raise TypeError("_filefullpathname must be str type")

        if len(_filefullpathname) == 0:
            raise ValueError("_filefullpathname must be set")

        basefilename = os.path.basename(_filefullpathname.rstrip('/'))
        # remove file extension name
        dot_pos = basefilename.find(".")
        if dot_pos == -1:
            textid = basefilename
        else:
            textid = basefilename[:dot_pos]

        return textid

    def _read_compound_words_dictionary(self):
        try:
            f = open("./com_dict.txt", "r")
        except IOError as e:
            f.close()
            return {}

        compound_text = f.read()
        dict = {}
        for d in [c.strip() for c in compound_text.split(",")]:
            dict_members = d.split(":")
            if len(dict_members) != 2:
                raise ValueError("compound text data is not valid")
            key_list = []
            for m in dict_members[0].split("+"):
                key_list.append(unicode(m))
            dict_key = tuple(key_list)
            dict_value = dict_members[1]
            dict[dict_key] = unicode(dict_value)

        return dict

    def _read_file_contents(self, _filename):
        #cdef list sents_full = []
        #cdef list sents_tx = []
        #cdef list sents_rx = []
        #cdef list sents_full_pos = []

        str_buffer = ""

        try:
            with open(_filename, "r") as f:
                str_buffer = f.read()
        except IOError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        try:
            str_buffer = self.utils.convert_encoding(str_buffer)
        except TypeError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        #str_buffer = str_buffer.replace("|", "\t")
        str_buffer = StringIO(str_buffer)

        try:
            df = pd.read_csv(str_buffer, sep='\||\t', header=None, engine='python')
            #df = pd.read_csv(str_buffer, sep='\t', header=None)
        except IOError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        df = df.set_index(df.columns[0])

        try:
            sents_full = df.iloc[:, 2].tolist()
            sents_full_pos = zip(df.iloc[:, 0].tolist(), df.iloc[:, 1].tolist())
        except KeyError, e:
            sents_full = []
            sents_full_pos = []

        try:
            sents_tx = df.loc[['TX']].iloc[:, 2].tolist()
        except KeyError, e:
            sents_tx = []

        try:
            sents_rx = df.loc[['RX']].iloc[:, 2].tolist()
        except KeyError, e:
            sents_rx = []

        return sents_full, sents_tx, sents_rx, sents_full_pos


    def _read_file_contents_mono(self, _filename):
        str_buffer = ""

        try:
            with open(_filename, "r") as f:
                str_buffer = f.read()
        except IOError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        try:
            str_buffer = self.utils.convert_encoding(str_buffer)
        except TypeError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        str_buffer = StringIO(str_buffer)

        try:
            df = pd.read_csv(str_buffer, sep='\t', header=None, engine='python')
        except IOError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        df = df.set_index(df.columns[0])

        try:
            sents_full = df.iloc[:, 1].tolist()
            #sents_full_pos = zip(df.iloc[:, 0].tolist(), df.iloc[:, 1].tolist())
            sents_full_pos = df.iloc[:, 0].tolist()
        except KeyError, e:
            sents_full = []
            sents_full_pos = []

        return sents_full, sents_full_pos


    def _read_db_contents(self, _sent):
        str_buffer = _sent

        try:
            str_buffer = self.utils.convert_encoding(str_buffer)
            str_buffer = str_buffer.replace("|", "\t")
            str_buffer = list(str_buffer.split('\n'))
            df = pd.DataFrame(str_buffer)
            df2 = pd.DataFrame(data=[x[0].split('\t') for x in df.values])
            # df3 = pd.DataFrame(data=[x[0].split('\t') for x in df2.values])
        except TypeError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        try:
            sents_full = (df2.iloc[:, 3].values).tolist()
            sents_full_pos = (df2.iloc[:, 1].values).tolist()
            sents_full_tx = (df2.iloc[:, 0].values).tolist()

        except KeyError, e:
            sents_full = []
            sents_full_pos = []
            sents_full_tx = []

        return sents_full, sents_full_pos, sents_full_tx


    def _read_db_contents_mono(self, _sent):
        str_buffer = _sent

        try:
            str_buffer = self.utils.convert_encoding(str_buffer)
            str_buffer = list(str_buffer.split('\r\n'))
            df = pd.DataFrame(str_buffer)
            df2 = pd.DataFrame(data=[x[0].split('\t') for x in df.values])
        except TypeError as e:
            self.logger.write_log("error", e.args)
            return [], [], []

        try:
            sents_full = (df2.iloc[:, 2].values).tolist()
            sents_full_pos = (df.iloc[:, :2].values).tolist()
        except KeyError, e:
            sents_full = []
            sents_full_pos = []

        return sents_full, sents_full_pos


    def do_reload_dictionary(self):
        try:
            self.c_dict = self._read_compound_words_dictionary()
            self.stop_words_list = self._read_stopwords()
        except Exception, e:
            msg = "exception occurred while reloading dictionary => {0}".format(e)
            self.logger.write_log("error", msg)
            raise e
        self.logger.write_log("info", "[[proc_base]] dictionary just loaded")

    @abstractmethod
    def do_processing(self): #, _filename_list):
        pass
