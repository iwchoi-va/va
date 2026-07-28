# -*- coding: utf-8 -*-
"""
    (C) Copyright Hansol Inticube 2017
        Writer : Jin Kak, Jung
        Revision History :
        First released on Apr, 30, 2017
"""
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
from konlpy.utils import pprint
import cython
import numpy as np

from tagger import tagger
from utils import log, utils
import weakref

import warnings as w
import datetime

class document_similarity(object):

    def __init__(self, _num_threads):
        self.utils = utils()
        self.tag = tagger()

        self.document_frequency = property(fget=weakref.ref(self.get_document_frequency), fset=weakref.ref(self.set_document_frequency))
        self.doc_ids= np.empty((_num_threads,), dtype=object) #empty : 초기화
        self.doc_all = np.empty((_num_threads,), dtype=object)
        self.vectorizer = np.empty((_num_threads,), dtype=object)
        self.tfidf = np.empty((_num_threads,), dtype=object)
        self.cos_similarities = np.empty((_num_threads,), dtype=object)
        self.related_sents_indice = np.empty((_num_threads,), dtype=object)

        print("[[ds]] ds object initialized")


    def __del__(self):
        print("[[ds]] ds object deinitialized")
        del self.tag
        del self.utils


    def _read_data(self, _filename):
        try:
            with open(_filename, 'r') as f:
                for line in f.read().splitlines():
                    yield line
        except IOError as e:
            raise e


    def get_document_frequency(self):
        try:
            return (self.min_df, self.max_df)
        except AttributeError as e:
            print(e)


    def set_document_frequency(self, _min_value, _max_value):
        self.min_df = _min_value
        self.max_df = _max_value


    """
        parameter : tuple that contains tuples ((id int, text string), (), ....)
    """
    def vectorizing(self, _thread_num, _vector_set):

        # FutureWarnings can be ignored: In import the necessary module, w.resetwarnings()
        w.simplefilter(action = 'ignore', category = FutureWarning)

        # Step1 (vectorizing rule sciprt)
        try:
            (_min_df, _max_df) = self.document_frequency
        except TypeError as e:
            raise TypeError('Set document frequency factor first' + e.args[0])

        rslt_ids = []
        rslt_sent = []
        rslt_ids_append = rslt_ids.append
        rslt_sent_append = rslt_sent.append
        for avec in _vector_set:
            SC_SNO, SC_LCLF_CD, SC_SCLF_CD, QST_CD, CNTR_CD, CTRA_INSA_CLCD, SCRIPT_SENT_ID, QST_RATE, QST_KEYWORD, SCRIPT_SENT = avec
            rslt_ids_append((SC_SNO, SC_LCLF_CD, SC_SCLF_CD, QST_CD, CNTR_CD, CTRA_INSA_CLCD, SCRIPT_SENT_ID, QST_RATE, QST_KEYWORD))
            rslt_sent_append(self.utils.convert_encoding(SCRIPT_SENT))

        self.doc_ids[_thread_num] = rslt_ids
        self.doc_all[_thread_num] = rslt_sent

        self.vectorizer[_thread_num] = TfidfVectorizer(tokenizer=self.tag.tokenizer, analyzer='word', min_df=_min_df, max_df=_max_df)
        self.tfidf[_thread_num] = self.vectorizer[_thread_num].fit_transform(self.doc_all[_thread_num])


    def get_similar_sentences(self, p_thread_num, _fileid, _sentence, _top = 5, _threshold = 0.6):

        new_document = [_sentence]
        new_tfidf = self.vectorizer[p_thread_num].transform(new_document) #읽어온 stt
        self.cos_similarities[p_thread_num] = linear_kernel(new_tfidf, self.tfidf[p_thread_num]).flatten()

        # find top n sentences
        self.related_sents_indice[p_thread_num] = self.cos_similarities[p_thread_num].argsort()[::-1] #-_top-1

        # query top n original sentences(exclude zero value similarity sentences)
        for idx in self.related_sents_indice[p_thread_num]:
            sc_SC_SNO, sc_SC_LCLF_CD, sc_SC_SCLF_CD, sc_QST_CD, sc_CNTR_CD, sc_CTRA_INSA_CLCD, sc_SCRIPT_SENT_ID, sc_QST_RATE, sc_QST_KEYWORD = self.doc_ids[p_thread_num][idx]
            #if sc_QST_CD=="100011380001":
            #    print(sc_SCRIPT_SENT_ID)
            if self.cos_similarities[p_thread_num][idx] >= _threshold:
                sc_SC_SNO, sc_SC_LCLF_CD, sc_SC_SCLF_CD, sc_QST_CD, sc_CNTR_CD, sc_CTRA_INSA_CLCD, sc_SCRIPT_SENT_ID, sc_QST_RATE, sc_QST_KEYWORD = self.doc_ids[p_thread_num][idx]
                yield sc_SC_SNO, sc_SC_LCLF_CD, sc_SC_SCLF_CD, sc_QST_CD, sc_CNTR_CD, sc_CTRA_INSA_CLCD, sc_QST_KEYWORD, sc_SCRIPT_SENT_ID, self.cos_similarities[p_thread_num][idx], new_document

        del new_document
        del new_tfidf
