sonardir=/opt/sonar-2.2
arch=macosx-universal-64
mvn clean install
rm $sonardir/extensions/plugins/sonar-sonargraph-plugin*
cp target/sonar-sonargraph-plugin*.jar $sonardir/extensions/plugins
$sonardir/bin/$arch/sonar.sh start

