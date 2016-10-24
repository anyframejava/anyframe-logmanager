@echo off
set ARGS=-Xmx512m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25 -Dfile.encoding=utf-8
java %ARGS% -jar anyframe-logmanager-packager.jar 8089 /logmanager