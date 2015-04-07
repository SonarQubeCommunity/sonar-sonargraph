set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphQuality/01_SonargraphQualityAndQualityBuildWithReporting.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.10:quality-create-snapshot
set DATA_DIR=C:\Users\Ingmar\.hello2morrow\pgdata
set INST_DIR=D:\Programs\Sonargraph-Quality-7.1.10
set PREFIX=AlarmClockMain 
set HOST=localhost
set PORT=3308
set QM_MODEL=%INST_DIR%/config/QualityModels/Standard_Review.qualitymodel
set CONF_DIR=D:\temp\SGQ_test\config
mvn  %SONARGRAPH_GOAL% -Dsonargraph.file=../AlarmClockMain.sonargraph -Dsonargraph.license=%LICENSE% -Dsonargraph.dataDirectory=%DATA_DIR% -Dsonargraph.installationDirectory=%INST_DIR% -Dsonargraph.prefix=%PREFIX% -Dsonargraph.host=%HOST% -Dsonargraph.port=%PORT% -Dsonargraph.qualityModel=%QM_MODEL% 