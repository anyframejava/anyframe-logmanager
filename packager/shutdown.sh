#!/bin/sh

Log=console.log
VERSION=1.6.0
ID=logmanagerweb

echo "Anyframe Log Manager Web $VERSION stopping..."

CNT=`ps -ef|grep "$ID"|grep -v grep|wc -l`
PROCESS=`ps -ef|grep "$ID"|grep -v grep|awk '{print $2}'`

if [ $CNT -ne 0 ]
then
  echo "process id : $PROCESS"
  kill -9 $PROCESS
else
  echo "Process already stopped."
fi

echo "Anyframe Log Manager Web $VERSION shutdown complete." >> $Log
echo "###########################################" >> $Log
echo "Anyframe Log Manager Web $VERSION shutdown complete."
echo "###########################################"