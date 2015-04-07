set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.2.1:architect-report
set DATA_DIR=C:\Users\Ingmar\.hello2morrow\pgdata
set INST_DIR=D:\00_workspace-sg-h2m\sonarj\target\dist\java
mvn clean package -e %SONARGRAPH_GOAL% -Dsonargraph.license=%LICENSE% -Dsonargraph.dataDirectory=%DATA_DIR% -Dsonargraph.installationDirectory=%INST_DIR%