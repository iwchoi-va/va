#! /usr/bin/python
# -*- coding: utf-8 -*-

from os import listdir
from os.path import isfile
import signal, sys, time
from apscheduler.schedulers.background import BackgroundScheduler

import pymssql

import json
import gc

from utils import log, utils
import datetime

class App():

    def __init__(self, _name):
        global process_name
        global log_filename

        process_name = _name
        self._read_config("./" + process_name + ".conf")

        log_filename = self.dir_log + "/" + process_name + ".log"


    def __enter__(self):
        self.logger = log(process_name.upper() + " main", log_filename)

        self.sched = BackgroundScheduler()

        self.logger.write_log("info", "[[spdbmain]] Enter to app")


    def __exit__(self, type, value, traceback):
        self.logger.write_log("info", "[[spdbmain]] Exit from app")
        self._release_resource()
        self.sched.shutdown()
        del self.sched


    def _release_resource(self):
        if (self.logger != None):
            del self.logger


    def _read_config(self, _filename):
        try:
            with open(_filename) as f:
                data = json.load(f)

            self.cores = data["parallel"]["cores"]

            self.old_db_host = data["old_db"]["host"]
            self.old_db_port = data["old_db"]["port"]
            self.old_db_database = data["old_db"]["database"]
            self.old_db_user = data["old_db"]["user"]
            self.old_db_pwd = data["old_db"]["pwd"]

            self.new_db_host = data["new_db"]["host"]
            self.new_db_port = data["new_db"]["port"]
            self.new_db_database = data["new_db"]["database"]
            self.new_db_user = data["new_db"]["user"]
            self.new_db_pwd = data["new_db"]["pwd"]

            self.dir_log = data["directory"]["log"]

            self.sch_day = data["scheduler"]["day_of_week"]
            self.sch_hour = data["scheduler"]["hour"]
            self.sch_minute = data["scheduler"]["minute"]

        except Exception as e:
            print 'exception : ', e
            raise e



    def sp_proc(self):
        data_set=[]
        # 1. GET!!
        try:
            read_tpano = self.getDataFromOld()
            if not read_tpano:
                self.logger.write_log("info", "[[sp_proc::read_tpano]] No Data form DB!")
                pass
            else:
                for row in read_tpano:
                    SpCode, TPANO, OrgFileName = row
                    #print(SpCode, TPANO, OrgFileName)
                    data_set.append((SpCode, TPANO, OrgFileName))
        except Exception, e:
            print("[[sp_proc]] DB Connectionn Fail!")
            return

        # 2. SET!!
        if len(data_set)>0:
            try:
                self.setDataToNew(data_set)
                self.logger.write_log("info", "[[sp_proc]] %s  complete setDataToNew" % (datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')))
            except Exception, e:
                msg = "exception setDataToNew => %s" % (e.args[0])
                self.logger.write_log("warning", msg)


    """
        Get 'TPANO' from NICE DB
    """
    def getDataFromOld(self):
        try:
            self.db = pymssql.connect(server=self.old_db_host, user=self.old_db_user, password=self.old_db_pwd, database=self.old_db_database)
            print("[[getDataFromOld]] DB INFO :: %s  %s  " % (self.old_db_host, self.old_db_database))
        except pymssql.DatabaseError, err:
            print("[[getDataFromOld]] DB Connectionn Failed, Check DB Server!")
            raise err
            pass
        now = datetime.datetime.now().strftime('%Y%m%d')
        with self.db.cursor() as cursor:
            cursor.execute("select a.SpCode, b.TPANO, a.OrgFileName  \
                              from ( select SpCode, OrgFileName, min(insdate) insdate  \
	                                   from Sponsor_CallInfo  \
                                      where SpCode in ('KS001','TT024') and InsDate >= %s and TPANO <>''  \
                                      group by SpCode, OrgFileName ) a inner join Sponsor_CallInfo b  \
                                on a.SpCode=b.SpCode and a.OrgFileName=b.OrgFileName and a.insdate=b.insdate ;", (now))
            resultset = cursor.fetchall()
            return resultset
        self.db.close()


    """
        Set 'TPANO' to NEW DB
    """
    def setDataToNew(self, _data_set):
        try:
            self.db = pymssql.connect(server=self.new_db_host, user=self.new_db_user, password=self.new_db_pwd, database=self.new_db_database)
            print("[[setDataToNew]] DB INFO :: %s  %s  " % (self.new_db_host, self.new_db_database))
        except pymssql.DatabaseError, err:
            print("[[setDataToNew]] DB Connectionn Failed, Check DB Server!")
            raise err
            pass

        with self.db.cursor() as cursor:
            sql = "update Sponsor_CallInfo \
                      set TPANO=%s, \
                          VA_FLAG = (CASE WHEN TPANO <> %s THEN 'N' ELSE VA_FLAG END) \
                    where SpCode=%s and OrgFileName=%s ;"
            for r_set in _data_set:
                #print(r_set[1], r_set[0], r_set[2])
                cursor.execute( sql, (r_set[1], r_set[1], r_set[0], r_set[2]) )
                self.db.commit()
        self.db.close()



    def register_jobs(self):
        self.logger.write_log("info", "[[spdbmain]] register jobs started")
        self.sched.add_job(self.sp_proc, 'cron', day_of_week=self.sch_day, hour=self.sch_hour, minute=self.sch_minute)


    def run(self):
        self.logger.write_log("info", "[[spdbmain]] Run")
        self.register_jobs()
        self.sched.start()
        while True:
            time.sleep(2)



if __name__=='__main__':

    reload(sys)
    sys.setdefaultencoding('utf-8')
    #sys.setdefaultencoding('euc-kr')

    app = App("spdb")

    with app:
        app.run()
