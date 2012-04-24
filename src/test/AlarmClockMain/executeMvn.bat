set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.5:architect-report 
mvn -e clean package %SONARGRAPH_GOAL% -Dsonargraph.license=%LICENSE% -Dsonargraph.prepareForSonar=true sonar:sonar