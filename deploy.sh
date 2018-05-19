sbt package &&
mv restApi/target/scala-2.11/wikiplagrestapi_2.11-0.1.0.war /opt/tomcat/webapps/ROOT.war &&
echo "!!! DONT FORGET: YOU MAY HAVE TO RESTART THE TOMCAT !!!"
