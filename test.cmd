rem Prove multi-thread tests get incorrect log
rem afterSpecification logs ending up in wrong log

gradlew clean test -Dtest.single=demo/LogbackLoggingIndex -Dconcordion.run.threadCount=5  -Dorg.gradle.java.home="C:\Program Files\Java\jdk1.8.0_121"

rem -Dlogback.configurationFile=logback-jenkins.xml  