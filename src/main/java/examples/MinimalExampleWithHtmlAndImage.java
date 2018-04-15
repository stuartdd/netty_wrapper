package examples;

import interfaces.Dispatcher;
import interfaces.HttpHandler;
import interfaces.Logger;
import java.io.File;
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
public class MinimalExampleWithHtmlAndImage {

    public static void main(String[] args) {
        /*
        The root of our web directory. All resources in a web application should be stored relative to this path.
        <root>/page/index.html
        <root>/image/stuart.png
        
        Never expose your main file system details by puting file paths in the HTML.
         */
        String root = System.getProperty("user.dir") + File.separator + "exampleweb";

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
                response.append("{\"action\" : \"Stopped\", \"msg\":\"Good Bye\"}");
                /*
                Tell the browser the response mime type is json ("application/json")
                 */
                response.setContentType(MimeTypes.getMimeType(".json"));
            }
        });
        /*
        Add a new route that will match /static/{anyValue}/{anyValue}
        
        The first * is the resource type (image or page)
        The second * is the file name (stuart.png or index.html).
        
        For example 'http://localhost:8888/static/page/index.html'
        For example 'http://localhost:8888/static/images/image1.png'
         */
        dispatcher.addRoute(new String[]{"static", "*", "*"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                /*
                Load the resource relative to the root directory: System.getProperty("user.dir")+File.separator+"exampleweb"
                <root>/page/index.html --> http://localhost:8888/static/page/index.html
                <root>/image/stuart.png --> <img src="/static/image/stuart.png" alt="Picture of Stuart" width="128" height="128">
                 */
                response.loadMedia(root, request.getPathSegments()[1], request.getPathSegments()[2], null);
            }
        });

        dispatcher.addRoute(new String[]{"data", "id", "*"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                /* 
                There are several API's for parsing JSON and XML. 
                I have not used them here to keep the app simple.
                The best I have found so far is https://github.com/FasterXML/jackson which has loads of extra stuff.
                 */
                response.append("{\"code\":999, \"message\":\"" + request.getPathSegments()[2] + "\"}");
                response.setContentType(MimeTypes.getMimeType(".json"));
            }
        });

        /*
        Last chance to do any thing before the server starts
         */
        System.out.println("From your browser http://localhost:8888/control/stop");
        System.out.println("From your browser: Test the html load - http://localhost:8888/static/page/index.html");
        System.out.println("From your browser: Test the data route - http://localhost:8888/data/id/abc123");

        /*
        Note the need to cast because the LoggerConfig interface does not have getFinalLogName().
         */
        System.out.println("Log file will he here:" + ((SimpleLoggerImpl) logger).getFinalLogName());
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
