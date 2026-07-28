#! /usr/bin/python
# -*- coding: utf-8 -*-
"""
    (C) Copyright Hansol Inticube Ltd,
        Writer : Jin Kak, Jung
        Revision history :
            First released on Apr, 30, 2017
"""
# Import OS related packages
from os import listdir
from os.path import isfile #, join
import signal, sys, time
from apscheduler.schedulers.background import BackgroundScheduler

# Import DS processing class
from ds_proc import ds_proc
#from app_base import app_base, directory_watcher

import json
import gc
import base64

from utils import log, utils
#import datetime

class App():

    def __init__(self, _name): #App 클래스를 선언한 시점에 동작하는 메소드
        global process_name #전역변수
        global log_filename #전역변수

        process_name = _name
        self._read_config("./config/" + process_name + ".conf")
        #now=datetime.datetime.now()
        #log_filename = self.dir_log + "/" + process_name + "_" + now.strftime('%Y%m%d')+".log"
        log_filename = self.dir_log + "/" + process_name + ".log"

        self.reload_dictionary = False
        self.re_loadscript = False

        # super(App, self).__init__(
        #         _use_directory_watcher = True, _directory_name = data_directory)

    def __enter__(self): #with문이 실행되면서 동작하는 메소드
        self.logger = log(process_name.upper() + " main", log_filename)

        self.dp = ds_proc(_num_threads=self.cores, _log_filename=log_filename, _parent=self,
                _mssqldb_host=self.rdb_host, _mssqldb_host_back=self.rdb_host_back, _mssqldb_port=self.rdb_port, _mssqldb_db=self.rdatabase, _mssqldb_user=self.rdb_user, _mssqldb_pwd=self.rdb_pwd)

        self.sched = BackgroundScheduler()


        self.dp.ds_options = (self.option_result_size, self.option_threshold)

        #self.utils.write_pid_of_process("hds")

        self.logger.write_log("info", "[[dsmain]] Enter to app")


    def __exit__(self, type, value, traceback): #모든 작업이 완료된 후에 동작하는 메소드
        self.logger.write_log("info", "[[dsmain]] Exit from app")
        self._release_resource()
        self.sched.shutdown()
        del self.sched


    def _release_resource(self):
        if (self.dp != None):
            del self.dp
        if (self.logger != None):
            del self.logger


    def _read_config(self, _filename):
        try:
            with open(_filename) as f:
                data = json.load(f)
            #----------------------------- core 수
            self.cores = data["parallel"]["cores"]
            #------------------------------db 계정 정보
            self.rdb_host = data["rdb"]["host"]
            self.rdb_host_back = data["rdb"]["host_back"]
            self.rdb_port = data["rdb"]["port"]
            self.rdatabase = data["rdb"]["database"]
            self.rdb_user = base64.decodestring(data["rdb"]["user"])
            self.rdb_pwd = base64.decodestring(data["rdb"]["pwd"])
            #------------------------------
            self.dir_log = data["directory"]["log"]
            self.dir_data = data["directory"]["data"]
            self.option_result_size = data["ds_options"]["result_size"]
            self.option_threshold = data["ds_options"]["threshold"] #가중치
            self.sch1_day = data["scheduler1"]["day_of_week"]
            self.sch1_hour = data["scheduler1"]["hour"]
            self.sch1_minute = data["scheduler1"]["minute"]
            self.sch2_day = data["scheduler2"]["day_of_week"]
            self.sch2_hour = data["scheduler2"]["hour"]
            self.sch2_minute = data["scheduler2"]["minute"]
            self.sch2_second = data["scheduler2"]["second"]


            #---------sample test ------------
            self.dir_sample = data["directory"]["sample"]
        except Exception as e:
            print 'exception : ', e
            raise e


    def register_jobs(self):
        self.logger.write_log("info", "[[dsmain]] register jobs started")
        # Read the all script once everyday
        #self.sched.add_job(self.dp.do_vectorizing, 'cron', day_of_week=self.sch1_day, hour=self.sch1_hour, minute=self.sch1_minute)
        # Read the stt every 10min

        #스케줄 등록하는 부분 --> 실행하는 스케줄, 방식, 주기
        self.sched.add_job(self.dp.do_processing, 'cron', day_of_week=self.sch2_day, hour=self.sch2_hour, minute=self.sch2_minute, second=self.sch2_second)


    def run(self):

        self.logger.write_log("info", "[[dsmain]] Run")
        self.register_jobs()
        self.sched.start()
        while True:
            time.sleep(2)

        """
        while True:
            self.logger.write_log("info", "[[dsmain]] Run")
            time.sleep(5)
            self.dp.do_processing()
        """



if __name__=='__main__':

    #module을 reload하기 위해 선언하는 부분인데, 해당부분은 python 3.5버전에서는 from imp import reload를 import 해준 후, reload(module_name)으로 변경해줘야한다.
    reload(sys)
    sys.setdefaultencoding('utf-8')

    app = App("ds")

    with app:
        app.run()

    #del app
    # sys.exit(0)
