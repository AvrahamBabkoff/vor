@echo off
@REM >output.txt (
@REM IF NOT EXIST C:\BIN\ (
@REM echo ##################################################################
@REM echo Creating folder C:\BIN\
@REM mkdir C:\BIN
@REM )
cd %~dp0\Services\VOSDalReport
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo copying VOSDalReport compiled folder
echo ##################################################################
@REM Xcopy %~dp0\Services\VOSDalData\target\VOSDalData-0.0.1-SNAPSHOT-production %~dp0\bin\VOSDalData-0.0.1-SNAPSHOT-production\ /E /Y
Xcopy %~dp0\Services\VOSDalReport\target\VOSDalReport-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y

@pause