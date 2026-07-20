#!/bin/sh
#
# vmstat 1 명령을 실행하며 날짜별로 로그를 만들면서 시각정보와 함께
# 10초 간격으로 내용을 기록하는 스크립트
#
# 원래소스 부분 : $AWK -v time=`date +%H:%M:%S` 'BEGIN{line="";}{line = $0;}END{ print time line; }' \

if [ -x /bin/nawk -o -x /usr/bin/nawk ];then
  AWK=nawk
elif [ -x /bin/awk -o /usr/bin/awk ];then
  AWK=awk
else
  echo "NOT FOUND nawk nor awk !!"
  exit 1
fi

while true
do
  vmstat 1 2 |\
  $AWK -v time=`date +%H:%M:%S` 'BEGIN{line="";}{line = $0;}END{ print time line; }' \
  >> vmstat_`date +%m%d`
  sleep 30
done
