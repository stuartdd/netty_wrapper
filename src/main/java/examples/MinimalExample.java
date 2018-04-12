package examples;

import interfaces.Dispatcher;
import interfaces.HttpHandler;
import interfaces.Logger;
import server.DispatcherImpl;
import server.HttpNettyRequest;
import server.HttpNettyResponse;
import server.HttpNettyServer;
import server.NettyConfigImpl;

/**
 *
 * @author stuartdd
 */
public class MinimalExample {

    public static void main(String[] args) {
        /*
        New config bean with it's port set.
         */
        NettyConfigImpl nettyConfig = new NettyConfigImpl();
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
        Last chance to do any thing before the server starts
        
        This is the only unnecessary line of code in this example!
        */
        System.out.println("From your browser http://localhost:8888/control/stop");
        /*
        Start the server and dont return untill the server is stopped!
        
        Note as we do not pass in a Logger all output will go to the console.
        */
        int rc = HttpNettyServer.run(dispatcher, nettyConfig);
        /*
        Server has stopped
         */
        System.exit(rc);
    }
}
