Google API translate for Java version 0.97 is not available in maven. So it has to be manually added to the local repository.

Installing a library in the local maven repository:
mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>

Select group id, artifact id and version as it appears in LogMap's pom file:
<dependency>
    <groupId>com.googlecode</groupId>
    <artifactId>google-api-translate-java</artifactId>
    <version>0.97</version>
</dependency>

e.g.:
mvn install:install-file -Dfile=/home/ernesto/git/logmap3-matcher/lib/google-api-translate-java-0.97.jar -DgroupId=com.googlecode -DartifactId=google-api-translate-java -Dversion=0.97 -Dpackaging=jar

In Windows you may need to add quotes for the groupId and version:
mvn install:install-file -Dfile=C:\path_to_logmap\logmap3-matcher\lib\google-api-translate-java-0.97.jar -DgroupId="com.googlecode" -DartifactId=google-api-translate-java -Dversion="0.97" -Dpackaging=jar
