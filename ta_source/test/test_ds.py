# -*- coding: utf-8 -*-
"""
    (C) Copyright Hansol Inticube 2017
        Writer : Jin Kak, Jung
        Revision History :
        First released on Apr, 30, 2017
"""
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
#from konlpy.utils import pprint
import cython
import numpy as np

from tagger import tagger
from utils import log, utils
import weakref

import warnings as w
import signal, sys, time

if __name__=='__main__':

    reload(sys)
    sys.setdefaultencoding('utf-8')

    w.simplefilter(action = 'ignore', category = FutureWarning)

    utils = utils()
    tag = tagger()

    (_min_df, _max_df) =  (0.1, 1.0)

    for idx, arg in enumerate(sys.argv[1:]) :
        if idx==0:
            train_set=[arg]
            print("[1] train_set")
            print(arg)
        elif idx==1:
            new_document=[arg]
            print("[2] test_set")
            print(arg)

    vectorizer = TfidfVectorizer(tokenizer=tag.tokenizer, analyzer='word', min_df=_min_df, max_df=_max_df)

    tfidf = vectorizer.fit_transform(train_set)

    new_tfidf = vectorizer.transform(new_document)

    cos_similarities = linear_kernel(new_tfidf, tfidf).flatten()

    print("[3] score")
    print(cos_similarities)
