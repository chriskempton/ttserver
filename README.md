## TappyTap Server

Requires (in addition to the dependencies in the pom)...

JDK 7+

[Apache Maven](http://maven.apache.org) 3.1

[Google Cloud Messaging](http://developer.android.com/reference/com/google/android/gcm/package-summary.html) jar (gcm-server.jar) to be installed in your local Maven repository using this command:

	mvn install:install-file -Dfile=<path-to-jar-directory>gcm-server.jar -DgroupId=com.google.android -DartifactId=gcm-server -Dversion=1.0 -Dpackaging=jar

You also need to create config.properties at the base of the project, with an APIKey property defined

To build, run

    mvn package

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this project.  Just run:

    mvn appengine:devserver
