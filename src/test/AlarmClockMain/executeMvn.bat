set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.10:architect-report
mvnDebug -e clean package %SONARGRAPH_GOAL% -Dsonargraph.license=%LICENSE% -Dsonargraph.prepareForSonar=true sonar:sonar