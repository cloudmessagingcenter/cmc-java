# cmc-java

Java library for communicating with the Cloud Messaging Center (CMC) REST API.

You can sign up for a Cloud Messaging Center (CMC) REST API account at https://www.cloudmessagingcenter.com/.

Prerequisites:
============

Java 1.7 and later

Installation
============

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.telecomsys.cmc</groupId>
  <artifactId>cmc-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "com.telecomsys.cmc:cmc-java:1.0.0"
```

### [ProGuard](http://proguard.sourceforge.net/)

If you're planning on using ProGuard, make sure that you exclude the CMC bindings. You can do this by adding the following to your `proguard.cfg` file:

    -keep class com.telecomsys.cmc.** { *; }

Usage
=====

