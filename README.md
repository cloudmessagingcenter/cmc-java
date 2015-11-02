# CMC REST API Java SDK  [![Build Status](https://travis-ci.org/cloudmessagingcenter/cmc-java.svg?branch=master)](https://travis-ci.org/cloudmessagingcenter/cmc-java)

Java library for communicating with the Cloud Messaging Center (CMC) REST API.

You can sign up for a Cloud Messaging Center (CMC) REST API account at https://www.cloudmessagingcenter.com/restsignup/.

Prerequisites:
============

Java 1.7 and later.

Installation
============

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.telecomsys.cmc</groupId>
  <artifactId>cmc-java</artifactId>
  <version>1.2.0</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "com.telecomsys.cmc:cmc-java:1.2.0"
```

### [ProGuard](http://proguard.sourceforge.net/)

If you're planning on using ProGuard, make sure that you exclude the CMC bindings. You can do this by adding the following to your `proguard.cfg` file:

    -keep class com.telecomsys.cmc.** { *; }

API Usage
=========

REST URL for trial: https://www.cloudmessagingtrial.com/rest/v1

REST URL for production: https://www.cloudmessaging.com/rest/v1

*	Import the classes into your code. For example,

```java
import com.telecomsys.cmc.*;
``` 

To send a message:
------------------

*	Create the API end point using the REST URL and the account ID (ACCOUNTID) and authentication token (AUTHTOKEN) you 
    receive in the REST sign-up email. For example to create the messaging end point:
    
```java
MessagingApi messagingApi = new MessagingApi("https://www.cloudmessagingtrial.com/rest/v1", ACCOUNTID, AUTHTOKEN);
```

*	Send the message to the destinations using the REST keyword (KEYWORD) receive in the REST sign-up email.

```java
List<String> destinations = new ArrayList<String>();
destinations.add(deviceNumber);
Message message = new Message(destinations, KEYWORD, "Test message");
HttpResponseWrapper<NotificationsResponse> sendMsgResponse = messagingApi.sendMessage(message);
```
