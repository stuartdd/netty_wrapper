# netty_wrapper
Classes to simplify Netty Server creation and configuration.

I found it very confusing to create and setup a Netty Server and as I have written many server systems over time I thought it needed a simplified interface model.

My target OS is Linux on a Raspberry PI. I needed a server to manage photos, a Samba file system with multiple users and a set of IOT nodes that act as a house alarm, heating management system media player etc. I initially tried GO which proved to be more than up to the task but as Java is my first language I found development much easier and faster. 

I came across Netty in work while using MockServer for testing and found it more scalable than native Java servers but lighter weight than Apache. The result is this wrapper for Netty.

The wrapper I have created uses a well known pattern. 

* Write minimal code to define and configure the server.
* Define a route (or path) to a process matching the http path elements.
* Write the processes.

When the path matches a route the processes is invoked with the required data objects populated.

The poiler plate code should be minimal yet allow for flexiblity when defining for performance and configuration.

Interfaces are used to allow the developer to use their own substituted implementations of key classes.
