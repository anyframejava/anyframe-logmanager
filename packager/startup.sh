#!/bin/sh

Log=console.log
VERSION=1.7.1-SNAPSHOT
ID=logmanagerweb

rm $Log

echo "###########################################" >> $Log
echo "Anyframe Log Manager Web $VERSION starting..."

ARGS="-D$ID"
nohup java $ARGS -jar anyframe-logmanager-packager.jar 8089 /logmanager >> $Log &

tail -f $Log