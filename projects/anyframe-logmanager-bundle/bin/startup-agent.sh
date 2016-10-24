#!/bin/sh

Log=console.log
VERSION=1.6.0
ID=logagent

CACHE=`find .. -name "meta-agent"`

if [ -n "$CACHE" ]
then
  rm -rf $CACHE
fi

rm -rf $Log

echo "###########################################" >> $Log
echo "Anyframe Log Manager Agent Service $VERSION starting..."

PRG="$0"

while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
 
PRGDIR=`dirname "$PRG"`
ARGS="-D$ID -Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25 -Djava.security.egd=file:/dev/urandom"
AGENT_DIR=../
LOGMANAGERF=`ls $PRGDIR | grep jar`
nohup java $ARGS -Dagent.dir=${AGENT_DIR} -jar ${PRGDIR}/${LOGMANAGERF} conf/logagent.ini >> $Log &

tail -f $Log