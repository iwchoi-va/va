rm -rf /home/MSENS/web/MSENS/webapps/temp/*.wav 2> /home/MSENS/web/MSENS/webapps/temp/file_errors.log

DATE=$(date '+%Y%m%d' -d '7 day ago')

rm -rf /home/MSENS/jeus7/domains/jeus_domain/servers/server1/logs/JeusServer_$DATE.log 2> /home/MSENS/web/MSENS/webapps/temp/file_errors.log

