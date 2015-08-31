rem set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphArchitect/SonargraphArchitect.license
set SONARGRAPH_GOAL=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.2.3:architect-report
set ACTIVATION_CODE=<activationCode>
mvn -e -Dsonargraph.activationCode=%ACTIVATION_CODE% -Dsonargraph.prepareForSonar=true clean package %SONARGRAPH_GOAL% sonar:sonar
