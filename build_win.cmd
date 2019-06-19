@echo off
@REM >output.txt (
@REM IF NOT EXIST C:\BIN\ (
@REM echo ##################################################################
@REM echo Creating folder C:\BIN\
@REM mkdir C:\BIN
@REM )
cd %~dp0\Utils\VOSUtilities
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Utils\VOSLogger
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Utils\VOSApiObjects
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Utils\VOSNetServer
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Utils\VOSNetClient
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Utils\VOSDBConnection
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Utils\jamod-svn-26-trunk
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean install

cd %~dp0\Services\VOSDalConfig
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo copying VOSDalConfig compiled folder
echo ##################################################################
@REM Xcopy %~dp0\Services\VOSDalConfig\target\VOSDalConfig-0.0.1-SNAPSHOT-production %~dp0\bin\VOSDalConfig-0.0.1-SNAPSHOT-production\ /E /Y
Xcopy %~dp0\Services\VOSDalConfig\target\VOSDalConfig-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y

cd %~dp0\Services\VOSDalData
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo copying VOSDalData compiled folder
echo ##################################################################
@REM Xcopy %~dp0\Services\VOSDalData\target\VOSDalData-0.0.1-SNAPSHOT-production %~dp0\bin\VOSDalData-0.0.1-SNAPSHOT-production\ /E /Y
Xcopy %~dp0\Services\VOSDalData\target\VOSDalData-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y

cd %~dp0\Services\VOSDalReport
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo copying VOSDalReport compiled folder
echo ##################################################################
@REM Xcopy %~dp0\Services\VOSDalData\target\VOSDalData-0.0.1-SNAPSHOT-production %~dp0\bin\VOSDalData-0.0.1-SNAPSHOT-production\ /E /Y
Xcopy %~dp0\Services\VOSDalReport\target\VOSDalReport-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y

cd %~dp0\Services\Modbus
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo ##################################################################
echo copying jar file
echo ##################################################################
@REM copy %~dp0\Services\Modbus\target\*.jar %~dp0\bin\ /Y
Xcopy %~dp0\Services\Modbus\target\Modbus-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y

cd %~dp0\Services\VOSFTPTM
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo ##################################################################
echo copying jar file
echo ##################################################################
@REM copy %~dp0\Services\VOSFTPTM\target\*.jar %~dp0\bin\ /Y
Xcopy %~dp0\Services\VOSFTPTM\target\VOSFTPTM-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y

cd %~dp0\apps\VOSSystemManager
echo ##################################################################
echo compileing %cd%
echo ##################################################################
call mvn clean compile assembly:single
echo ##################################################################
echo copying jar file
echo ##################################################################
@REM copy %~dp0\apps\VOSSystemManager\target\*.jar %~dp0\bin\ /Y
Xcopy %~dp0\apps\VOSSystemManager\target\VOSSytemManager-0.0.1-SNAPSHOT-production %~dp0\bin\ /E /Y
@REM )
@pause
