@echo off
@REM >output.txt (
@REM IF NOT EXIST C:\BIN\ (
@REM echo ##################################################################
@REM echo Creating folder C:\BIN\
@REM mkdir C:\BIN
@REM )

cd %~dp0\apps\VOSSystemManager
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo ##################################################################
echo coping jar file
echo ##################################################################
@REM copy %~dp0\apps\VOSSystemManager\target\*.jar %~dp0\bin\ /Y
Xcopy %~dp0\apps\VOSSystemManager\target\VOSSytemManager-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y
@REM )
@pause
