set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphQuality/SonargraphQuality_AndBuild.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.10:quality-start-local-database-server
set DATA_DIR=C:\Users\Ingmar\.hello2morrow\pgdata
set INST_DIR=D:\Programs\Sonargraph-Quality-7.1.10
mvn -e %SONARGRAPH_GOAL% -Dsonargraph.license=%LICENSE% -Dsonargraph.dataDirectory=%DATA_DIR% -Dsonargraph.installationDirectory=%INST_DIR%