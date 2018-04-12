# netty_wrapper
Classes to simplify Netty Server creation and configuration.

I found it very confusing to create and setup a Netty Server and as I have written many server systems over time I thought it needed a simplified interface model.

My target OS is Linux on a Raspberry PI. I needed a server to manage photos, a Samba file system with multiple users and a set of IOT nodes that act as a house alarm, heating management system media player etc. I initially tried GO which proved to be more than up to the task but as Java is my first language I found development much easier and much faster. 

I came across Netty in work while using MockServer for testing and found it more scalable than native Java servers but lighter weight than Apache. The result is this wrapper for Netty.

The wrapper I have created uses a well known pattern. 

* Write minimal code to define and configure the server.
* Define a route (or path) to a process matching the http path elements.
* Write the processes.

When the path matches a route the processes is invoked with the required data objects populated.

The poiler plate code should be minimal yet still allow for flexiblity when designing for performance and detailed configuration.

Interfaces are used throughtout to allow the developer to use their own substituted implementations of key classes.
## Configuration
Java Beans are passed in to provide configuration data to the Netty server and the Logging classes. Interfaces are used to define a minimum set of properties and sensible defaults are provided in the implementaton classes. The only required property for server configuration is the port, the rest can be left to their default values.
## Routes
These are used to provide a match on request paths. Wild cards and multiple elements are supported.

The path:```http://localhost:8888/a/b/c```
Will match the route ```"a", "b" ,"c"```

The path:```http://localhost:8888/a/b/any```
Will match the route ```"a", "b" ,"*"``` or any value in 'any'

It will not match ```http://localhost:8888/a/b/any/d```

The path:```http://localhost:8888/a/b/any/d```
Will match the route ```"a", "b" ,"..."``` or any value or number of values after the /a/b/.

For example it will match ```http://localhost:8888/a/b/any/d/e/f```

The '*' means match any value at that position.

The '...' means match an number of values at that position and the following positions. There is not point adding any thing after the '...'

**Every route that is defined must have a matching handler**.
## Handlers 
These are hooked in to the server with the routes via the Dispatcher. When you define a route using the Dispatcher you define a handler. When the route matches the path elements the handler is called.

A handler can be any class that implements **HttpHandler**. **HttpHandler** requires a single method to be implemented:

```java
void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger);
```
## Request and Response
When a handler is called the request data is packaged in to a **HttpNettyRequest**. This provides easy access to the data in the request.

When your logic in the handler completes it adds the response to the **HttpNettyResponse** and exits the handler method.
## logger
If no logger was provided for the server a default logger is created and passed to all handlers. The default logger will output to the console. 

It is better to use the provided logger than System.out. If a different logger is used later the code would not be able to use it if it used System.out. The logger also timestamps each line of output. See Logging below.
## Response Media
The **HttpNettyResponse** class has a method that will load media (images, css, scripts, html etc) from a root web directory. There is a full example in **MinimalExampleWithHtmlAndImage**
## Dispatcher
This provides the glue between the Netty server, the routes and the business logic (handlers).
## Exceptions
Default exception handling will catch exceptions thrown by the handlers. These are returned to the browser as follows:

This example shows what is returned when a media resource can not be found:
```json
{"Status":404, "Msg":"Not Found", "Entity":"2018-04-12T15:50:51.142:Resource 'index.html' was not found"}
```
## Logging
A simple logger interface is provided (with it's own configuration bean). The developer can use the simple logger implementation provided or substitute their own as required.
## Server
The server requires a Dispatcher, a configuration bean and an optional Logger. Default implementations of these are provided.
## Build
The netty_wrapper is provided as a Gradle project.
* Clone the project to Windows or Linux platforms. No advanced knowledge of gradle is required.

```bash
cd to_an_empty_directory
git clone https://github.com/stuartdd/netty_wrapper
```

* Build using the provided gradle build scripts. The resulting JAR is complete with ALL dependencies built in.

```
cd netty_wrapper
./gradlew clean fatjar
```
```
cd netty_wrapper
gradlew.bat clean fatjar
```

The resulting JAR is **netty_wrapper/build/libs/serverJarWithDependencies-1.0.jar**

## Test
There are tests defined for the server. They test the netty-wrapper functionality. To run these 

```
cd netty_wrapper
./gradlew clean test
```

## Deploy
If you have a Maven repository used by Maven or Gradle projects you can deploy to it as follows

```
cd netty_wrapper
./gradlew publish
```

* Create your project and add the JAR as a dependency.
