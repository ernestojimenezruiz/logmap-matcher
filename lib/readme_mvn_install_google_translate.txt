Google API translate for Java version 0.97 is not available in maven. So it has be to be added manually with 
the maven install command,

Installing library in the local maven repository:

mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>


Select group id, artifact id and version as it appears in the pom:
<dependency>
    <groupId>com.googlecode</groupId>
    <artifactId>google-api-translate-java</artifactId>
    <version>0.97</version>
</dependency>

e.g.:
mvn install:install-file -Dfile=/home/ernesto/git/logmap3-matcher/lib/google-api-translate-java-0.97.jar -DgroupId=com.googlecode -DartifactId=google-api-translate-java -Dversion=0.97 -Dpackaging=jar

