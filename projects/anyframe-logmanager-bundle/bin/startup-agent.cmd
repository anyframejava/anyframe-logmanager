@echo off

if exist ..\meta-agent (
 rd /S/Q ..\meta-agent
)

set AGENT_DIR=../
set ARGS=-Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25

java %ARGS% -Dagent.dir=%AGENT_DIR% -jar anyframe-logmanager-bundle-1.7.1-SNAPSHOT.jar conf/logagent.ini