#!/bin/sh

work_path=`pwd`;

crontab -l > cron.txt;

while read line; do
	if [[ "$line" = *"$work_path/del_log.sh"* ]]; then
		grep -v "$work_path/del_log.sh" cron.txt > cron_new.txt;
		crontab cron_new.txt
		rm cron_new.txt
		rm cron.txt
		echo "crontab schedule unregistered.";
		exit
	fi

done < cron.txt
