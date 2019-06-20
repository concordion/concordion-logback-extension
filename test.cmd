# Prove multi-thread tests get incorrect log
# afterSpecification logs ending up in wrong log

gradlew clean test -Dtest.single=demo/LogbackLoggingIndex -Dlogback.configurationFile=logback-jenkins.xml -Dconcordion.run.threadCount=5