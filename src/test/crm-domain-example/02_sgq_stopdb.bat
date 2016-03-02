set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphQuality/01_SonargraphQualityAndQualityBuildWithReporting.license
set SGQ_STOP_DB_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.9:quality-stop-local-database-server
set DATA_DIR=C:\Users\Ingmar\.hello2morrow\pgdata
set INST_DIR=D:\Programs\Sonargraph-Quality7.1.9

mvn -e %SGQ_STOP_DB_GOAL% -Dsonargraph.license=%LICENSE% -Dsonargraph.dataDirectory=%DATA_DIR% -Dsonargraph.installationDirectory=%INST_DIR%