## TappyTap Server

Requires [Apache Maven](http://maven.apache.org) 3.1 or greater, and JDK 7+ in order to run.

Requires [Google Cloud Messaging](http://developer.android.com/reference/com/google/android/gcm/package-summary.html) jar (gcm-server.jar) to be installed in your local Maven repository using this command:

mvn install:install-file -Dfile=<path-to-jar-directory>gcm-server.jar -DgroupId=com.google.android -DartifactId=gcm-server -Dversion=1.0 -Dpackaging=jar

You also need to create config.properties at the base of the project, with an APIKey property defined

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

For further information, consult the [Java App Engine](https://developers.google.com/appengine/docs/java/overview) documentation.

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine
