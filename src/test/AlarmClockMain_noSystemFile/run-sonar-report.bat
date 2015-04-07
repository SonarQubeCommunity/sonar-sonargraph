set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.2.2:architect-report 
rem mvn -e clean package %SONARGRAPH_GOAL% -Dsonargraph.license=%LICENSE% -Dsonargraph.prepareForSonar=true -Dsonargraph.useSonargraphWorkspace=true sonar:sonar 
mvn -e clean package sonar:sonar 