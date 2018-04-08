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
These are used to provide a match on requets path. Wild cards and multiple elements are supported.
## Dispatcher
This provides the glue between the Netty server, the routes and the business logic (handlers).
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
Ther are test defined for the server. To run these 

```
cd netty_wrapper
./gradlew clean test
```

* Create your project and add the JAR as a dependency.
