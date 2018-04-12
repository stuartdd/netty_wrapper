package server;

import interfaces.HttpHandler;
import interfaces.Dispatcher;
import interfaces.Logger;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author stuartdd
 */
public class ServerBaseTest {

    private static final int EXPECTED_RETURN_CODE = 99;
    private static final int PORT = 8888;
    private static final int DELAY = 100;
    private static final String LOG[] = new String[]{
        "Server started on port " + PORT,
        "REQ : uri[/server/ping] method[GET]",
        "RESP: text/plain; charset=UTF-8 [PING]",
        "REQ : uri[/server/stop] method[GET]",
        "Shutting down in " + DELAY + " Milliseconds. Reason: [Test Shutdown] Return code: ["+EXPECTED_RETURN_CODE+"]",
        "RESP: text/plain; charset=UTF-8 [Shutdown request accepted]",
        "SHUT DOWN 'Test Shutdown' EXECUTED. RC: 99"
    };

    @Test
    public void test() {
        Assert.assertEquals("PING", TestTools.getResponseFromServer("server/ping"));
        Assert.assertEquals("Shutdown request accepted", TestTools.getResponseFromServer("server/stop"));
        /*
        Need to wait for the server to shut down or we will miss the end of the logs.
         */
        TestTools.sleep(500);
        /**
         * Now we can get the logs and make sure all the messages are present.
         */
        System.out.println(TestTools.getLogger());
        TestTools.assertLoggerContains(LOG);
    }

    @After
    public void after() {
        Assert.assertNull(TestTools.getError(), TestTools.getError());
    }
    
    @Before
    public void before() {
        /*
        Minimal config.
         */
        NettyConfigImpl nettyConfig = new NettyConfigImpl();
        nettyConfig.setLogRequestResponse(true);
        nettyConfig.setPort(PORT);

        /*
        Minimal dispatcher
         */
        Dispatcher dispatcher = new DispatcherImpl();

        /*
        Add a Route to stop the server (at the end of testing)
         */
        dispatcher.addRoute(new String[]{"server", "stop"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                /*
                Shut the server down.
                If you find the server shuts down befor the client has received the response you can increase DELAY.
                 */
                HttpNettyServer.shutDown("Test Shutdown", DELAY, EXPECTED_RETURN_CODE);
                /*
                There is still time to respond.
                 */
                response.append("Shutdown request accepted");
            }
        });

        dispatcher.addRoute(new String[]{"server", "ping"}, new HttpHandler() {
            @Override
            public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
                response.append("PING");
            }
        });
        TestTools.runServerThread(dispatcher, nettyConfig, EXPECTED_RETURN_CODE);
    }

}
