set LICENSE=C:/Users/Ingmar/.hello2morrow/SonargraphQuality/01_SonargraphQualityAndQualityBuildWithReporting.license
set DATA_DIR=C:\Users\Ingmar\.hello2morrow\pgdata
set INST_DIR=D:\Programs\Sonargraph-Quality7.1.9

set PREFIX=com.hello2morrow.sonargraph:maven-sonargraph-plugin:7.1.9
set SGQ_GOALs=%PREFIX%:quality-create-snapshot %PREFIX%:quality-report %PREFIX%:quality-delete-older-snapshots
set SOURCE="-Dsonargraph.storeSourceCode=true"
set QM_MODEL="-Dsonargraph.qualityModel=%INST_DIR%/config/QualityModels/Standard_Review.qualitymodel"
set DAYS_TO_KEEP="-Dsonargraph.daysToKeep=7"
set WEEKS_TO_KEEP="-Dsonargraph.daysToKeep=4"
set MONTHS_TO_KEEP="-Dsonargraph.monthsToKeep=12"
set WORKSPACE="-Dsonargraph.useSonargraphWorkspace=true"

mvn -e install %SONARGRAPH_GOALS% -Dsonargraph.license=%LICENSE% -Dsonargraph.dataDirectory=%DATA_DIR% -Dsonargraph.installationDirectory=%INST_DIR% %SOURCE% %QM_MODEL% %DAYS_TO_KEEP% %WEEKS_TO_KEEP% %MONTHS_TO_KEEP% %WORKSPACE%