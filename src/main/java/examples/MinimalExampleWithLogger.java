package examples;

import interfaces.Dispatcher;
import interfaces.HttpHandler;
import interfaces.Logger;
import server.DispatcherImpl;
import server.HttpNettyRequest;
import server.HttpNettyResponse;
import server.HttpNettyServer;
import server.MimeTypes;
import server.NettyConfigImpl;
import server.logging.SimpleLoggerImpl;
import server.logging.SimpleLoggingConfig;

/**
 *
 * @author stuartdd
 */
public class MinimalExampleWithLogger {

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
        NettyConfigImpl nettyConfig = new NettyConfigImpl();
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
                The runReturnCode will be returned from the run method.
                Note shutDown does not wait for server to shut down. It returns immediatly.
                 */
                HttpNettyServer.shutDown("Good bye", 100, 0);
                /*
                The respone should appear in the browser!
                 */
                response.append("{\"msg\":\"Good Bye\"}");
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
        int rc = HttpNettyServer.run(dispatcher, nettyConfig, logger);
        /*
        Server has stopped
         */
        System.exit(rc);
    }
}
