@echo off

if exist ..\meta-agent (
 rd /S/Q ..\meta-agent
)

set ARGS=-Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25
java %ARGS% -jar anyframe-logmanager-bundle-1.5.0.jar conf/logagent.ini