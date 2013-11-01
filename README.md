## TappyTap Server

Requires (in addition to the dependencies in the pom)...

JDK 7+

[Apache Maven](http://maven.apache.org) 3.1

You also need to create config.properties at the base of the project, with an APIKey property defined

To build, run

    mvn package

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this project.  Just run:

    mvn appengine:devserver
