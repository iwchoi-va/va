#!/bin/sh

work_path=`pwd`;

crontab -l > cron.txt;

echo "0 3 * * 1 $work_path/del_log.sh > /dev/null 2>&1" >> cron.txt

crontab cron.txt

echo "crontab schedule registered.";

rm cron.txt
