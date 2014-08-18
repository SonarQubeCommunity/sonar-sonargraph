set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.10:architect-report
mvnDebug -e -Dsonargraph.useSonargraphWorkspace=true -Dsonargraph.license=%LICENSE% -Dsonargraph.prepareForSonar=true clean package %SONARGRAPH_GOAL% sonar:sonar