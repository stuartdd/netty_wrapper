package server;

import interfaces.Dispatcher;
import interfaces.Logger;
import interfaces.LoggerConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import junit.framework.Assert;
import server.logging.SimpleLoggerImpl;

/**
 *
 * @author stuartdd
 */
public class TestTools {

    private static Logger logger;
    private static ServerThread serverThread;
    private static String error;

    public static Logger getLogger() {
        return logger;
    }

    public static ServerThread getServerThread() {
        return serverThread;
    }

    public static String getError() {
        return error;
    }

    public static void setError(String error) {
        TestTools.error = error;
    }

    public static void assertLoggerContains(String[] list) {
        for (String s : list) {
            if (!getLogger().toString().contains(s)) {
                Assert.fail("Log [" + getLogger().toString() + "] does not contain [" + s + "]");
            }
        }
    }

    /**
     * Start the server in a thread.
     *
     * Creates a logger for you.
     *
     * @param dispatcher You need a Dispatcher
     * @param newNettyConfig You need a NettyConfig
     */
    public static void runServerThread(Dispatcher dispatcher, NettyConfigImpl nettyConfig, int expectedRunReturnCode) {
        /*
        Create the logger and keep it for later
         */
        logger = createLogger();
        /*
        Create and start the server thread.
         */
        serverThread = new ServerThread(dispatcher, nettyConfig, logger, expectedRunReturnCode);
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
    }

    /**
     * Class that runs the server in a thread. You need a Dispatcher a
     * NettyConfig and a Logger object to make it work.
     *
     */
    public static class ServerThread extends Thread {

        private final Dispatcher dispatcher;
        private final NettyConfigImpl nettyConfig;
        private final Logger logger;
        private int expectedRunReturnCode = 0;
        private boolean starting = true;

        public ServerThread(Dispatcher dispatcher, NettyConfigImpl nettyConfig, Logger logger, int expectedRunReturnCode) {
            this.dispatcher = dispatcher;
            this.nettyConfig = nettyConfig;
            this.logger = logger;
            this.expectedRunReturnCode = expectedRunReturnCode;
            TestTools.setError(null);
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
             * This is why we need to run it in a thread. Otherwise we could not
             * get any testing done!
             */
            int runReturnCode = HttpNettyServer.run(dispatcher, nettyConfig, logger);
            if (runReturnCode != expectedRunReturnCode) {
                TestTools.setError("Run Return Code is incorrect! expected[" + expectedRunReturnCode + "] actual [" + runReturnCode + "]");
            }
        }

        public NettyConfigImpl getNettyConfig() {
            return nettyConfig;
        }
    }

    /**
     * Simply sleep for ms milliseconds
     *
     * @param ms the milliseconds!
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Create a logger that holds on to the logged data so we can get it later!
     *
     * @return A logger for testing.
     */
    private static Logger createLogger() {
        return new Logger() {
            StringBuilder logData = new StringBuilder();

            @Override
            public void init(LoggerConfig myNettyConfig) {
                logData.setLength(0);
            }

            @Override
            public void log(String message) {
                logData.append(message).append('|');
            }

            @Override
            public void log(String message, Throwable ex) {
                logData.append(message).append('|').append(SimpleLoggerImpl.toStringException(ex)).append('|');
            }

            @Override
            public String toString() {
                return logData.toString();
            }
        };
    }

    /**
     * Sent a http get request to the server
     *
     * @param s The path that comes after the host.
     * @return The response from the server.
     */
    public static String getResponseFromServer(String s) {
        try {
            URLConnection connection = new URL("http://localhost:" + getServerThread().getNettyConfig().getPort() + "/" + s).openConnection();
            connection.setDoOutput(false);
            StringBuilder sb = new StringBuilder();
            InputStream response = connection.getInputStream();
            while (response.available() > 0) {
                sb.append((char) response.read());
            }
            return sb.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
