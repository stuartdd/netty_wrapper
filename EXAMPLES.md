# Examples
Examples are in the **server/src/main/java/examples**
## Minimal Example
```java
    public static void main(String[] args) {
        /*
        New config bean with it's port set.
         */
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setPort(8888);
        /*
        New dispatcher with a route to '/control/stop' and the handler to call when the request matches the route
         */
        Dispatcher dispatcher = new DispatcherImpl();
        dispatcher.addRoute(new String[]{"control", "stop"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                /*
                This is the code executed for http://localhost:8888/control/stop
                
                Wait 100 milliseconds then shut the server down. The message 'Good bye' will appear in the logs.
                Note shutDown does not wait for server to shut down. It returns immediatly.
                 */
                HttpNettyServer.shutDown("Good bye", 100);
                /*
                The respone should appear in the browser!
                */
                response.append("BYE");
            }
        });
        /*
        Last chance to do any thing before the server starts
        */
        System.out.println("From your browser http://localhost:8888/control/stop");
        /*
        Start the server and dont return untill the server is stopped!
        
        Note as we do not pass in a Logger all output will go to the console.
        */
        HttpNettyServer.run(dispatcher, nettyConfig);
        /*
        Server has stopped
        */
    }
```
## Minimal Example Easy Read
```java
    public static void main(String[] args) {
        /*
        New config bean with it's port set.
         */
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setPort(8888);
        /*
        New dispatcher with a route to '/control/stop' and the handler to call when the request matches the route
         */
        Dispatcher dispatcher = new DispatcherImpl();
        /*
        The handler is defined as a new class below 'StopHandler()'.
        */
        dispatcher.addRoute(new String[]{"control", "stop"}, new StopHandler());
        /*
        Last chance to do any thing before the server starts
        
        This is the only unnecessary line of code in this example!
         */
        System.out.println("From your browser http://localhost:8888/control/stop");
        /*
        Start the server and dont return untill the server is stopped!
        
        Note as we do not pass in a Logger all output will go to the console.
         */
        HttpNettyServer.run(dispatcher, nettyConfig);
        /*
        Server has stopped
         */
    }

    /**
     * Implement HttpHandler so it must define 
     *      handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger)
     * 
     * This would normally be in a package called handlers but that is up to the developer
     */
    public static class StopHandler implements HttpHandler {

        @Override
        public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
            /*
                This is the code executed for http://localhost:8888/control/stop
                
                Wait 100 milliseconds then shut the server down. The message 'Good bye' will appear in the logs.
                Note shutDown does not wait for server to shut down. It returns immediatly.
             */
            HttpNettyServer.shutDown("Good bye", 100);
            /*
                The respone should appear in the browser!
             */
            response.append("BYE");
        }

    }
```
## Minimal Example with Logger
```java
    public static void main(String[] args) {
        /*
        Create a Configuration bean for the logger and set it's file name.
        
        Note the %{ts} will be replaced by a TimeStanp formatted by the value:
            loggerConfig.getTimeStamp(). (yyyy_MM_dd_HH_mm_ss)
        The method: 
            loggerConfig.timeStampFormattedAsString() is used to return the value.
         */
        SimpleLoggingConfig loggerConfig = new SimpleLoggingConfig();
        loggerConfig.setLogFileName("MinimalExample_%{ts}.log");
        /*
        Create the logger and configure it.
        */
        Logger logger = new SimpleLoggerImpl();
        logger.init(loggerConfig);
        /*
        New config bean with it's port set.
         */
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setPort(8888);
        /*
        This option causes the request response pairs to be logged.
        Note - Only the first 100 characters of the response body are logged.
         */
        nettyConfig.setLogRequestResponse(true);
        /*
        New dispatcher with a route to '/control/stop' and the handler to call when the request matches the route
         */
        Dispatcher dispatcher = new DispatcherImpl();
        dispatcher.addRoute(new String[]{"control", "stop"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                /*
                This is the code executed for http://localhost:8888/control/stop
                
                Wait 100 milliseconds then shut the server down. The message 'Good bye' will appear in the logs.
                Note shutDown does not wait for server to shut down. It returns immediatly.
                 */
                HttpNettyServer.shutDown("Good bye", 100);
                /*
                The respone should appear in the browser!
                 */
                response.append("{\"action\" : \"Stopped\"}");
                /*
                Tell the browser the response mime type is json ("application/json")
                */
                response.setContentType(MimeTypes.getMimeType(".json"));
            }
        });
        /*
        Add a new route that will match /name/{anyValue}
        */
        dispatcher.addRoute(new String[]{"name", "*"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                /*
                Respond with a small JSON String returning the name.
                */
                response.append("{\"name\" : \""+request.getPathSegments()[1]+"\"}");
                /*
                Tell the browser the response mime type is json ("application/json")
                */
                response.setContentType(MimeTypes.getMimeType(".json"));
            }
        });
        /*
        Last chance to do any thing before the server starts
        */
        System.out.println("From your browser http://localhost:8888/control/stop");
        System.out.println("From your browser http://localhost:8888/name/Fred");
        /*
        Note the need to cast because the LoggerConfig interface does not have getFinalLogName().
        */
        System.out.println("Log file will he here:"+((SimpleLoggerImpl)logger).getFinalLogName());
        /*
        Start the server and dont return untill the server is stopped!
        
        Note as we do not pass in a Logger all output will go to the console.
         */
        HttpNettyServer.run(dispatcher, nettyConfig, logger);
        /*
        Server has stopped
         */
    }

```
### Log contains
```log
2018_04_08_14_36_02:Log created!
2018_04_08_14_36_02:Server started on port 8888
2018_04_08_14_36_18:   (13) REQ : uri[/name/Fred] method[GET]
2018_04_08_14_36_18:   (13) RESP: application/json [{"name" : "Fred"}]
2018_04_08_14_36_25:   (13) REQ : uri[/control/stop] method[GET]
2018_04_08_14_36_25:Shutting down in 100 Milliseconds. Reason: [Good bye]
2018_04_08_14_36_25:   (13) RESP: application/json [{"action" : "Stopped"}]
2018_04_08_14_36_25:SHUT DOWN 'Good bye' EXECUTED
```
## Run the server in a thread
Note ALL of the following code is from the TestTools class:
```
netty_wrapper/src/test/java/server/TestTools.java
```

Create the ServerThread class below and use the following code to start it.
```java
    serverThread = new ServerThread(dispatcher, nettyConfig, logger);
    serverThread.start();
    /*
    Wait for it to stop starting!
    */
    while (serverThread.isStarting()) {
        sleep(100);
    }
    /*
    Give it some time to settle.
    */
    sleep(100);
```

Where sleep(n) is defined as follows

```java
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

```

```java
    public class ServerThread extends Thread {

        private final Dispatcher dispatcher;
        private final NettyConfigImpl nettyConfig;
        private final Logger logger;
        private boolean starting = true;

        public ServerThread(Dispatcher dispatcher, NettyConfigImpl nettyConfig, Logger logger) {
            this.dispatcher = dispatcher;
            this.nettyConfig = nettyConfig;
            this.logger = logger;
        }

        public boolean isStarting() {
            return starting;
        }

        @Override
        public void run() {
            starting = false;
            /**
             * This method blocks does not return until the server stops.
             * 
             * This is why we need to run it in a thread. Otherwise we could not get any testing done!
             */
            HttpNettyServer.run(dispatcher, nettyConfig, logger);
        }

        public NettyConfigImpl getNettyConfig() {
            return nettyConfig;
        }
    }

```
