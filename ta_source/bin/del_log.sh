#!/bin/sh

find /log/ta_log/* -mtime +13 -exec rm {} \;
