package server;

import interfaces.HttpHandler;
import interfaces.Dispatcher;
import interfaces.Logger;
import org.junit.Test;

/**
 *
 * @author stuartdd
 */
public class SimpleServerNulls {
    private Dispatcher dispatcher = new Dispatcher() {
            @Override
            public void addRoute(String[] path, HttpHandler handler) {
            }

            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
            }
        };
    
    private NettyConfigImpl nettyConfig = new NettyConfigImpl();

    @Test(expected = ServerSetupException.class)
    public void simpleServerWithNullDispatcherAndConfig() {
        HttpNettyServer.run(null, null);
    }
    
    @Test(expected = ServerSetupException.class)
    public void simpleServerWithNullConfig() {
        HttpNettyServer.run(dispatcher, null);
    }
    
    @Test(expected = ServerSetupException.class)
    public void simpleServerWithNullDispatcher() {
         HttpNettyServer.run(null, nettyConfig);
    }
    
    @Test(expected = ServerSetupException.class)
    public void simpleServerWithPortNotSet() {
         HttpNettyServer.run(dispatcher, new NettyConfigImpl());
    }
            
}
