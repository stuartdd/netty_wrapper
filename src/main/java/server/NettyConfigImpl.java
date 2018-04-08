package server;

import interfaces.NettyConfig;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Configure the Netty Server and a load of other stuff!
 *
 * @author stuartdd
 */
public class NettyConfigImpl implements NettyConfig {

    /**
     * The port to start the server on.
     */
    private int port = 0;
    /**
     * A delay before the server becomes active after loading. I found this
     * useful if I had other services to set up and was running the server in
     * it's own thread.
     */
    private long startupDelayMs = 0;
    /**
     * The ssl property use by HttpNettyServer
     */
    private boolean ssl = false;
    /**
     * An object used define a date time format. Not used by this package
     */

    private String dateTimeFormat = "dd:MM:yyyy HH-mm-ss";
    private boolean logRequestResponse = false;
    /**
     * An object used define a timestamp. Not used by this package
     */
    private String timestamp = "dd:MM:yyyy HH-mm-ss";
    /**
     * Number of Boss event loop threads. See Netty documentation.
     */
    private int bossEventLoopThreads = 4;
    /**
     * Number of Worker event loop threads. See Netty documentation.
     */
    private int workerEventLoopThreads = 4;

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public long getStartupDelayMs() {
        return startupDelayMs;
    }

    public void setStartupDelayMs(long startupDelayMs) {
        this.startupDelayMs = startupDelayMs;
    }

    @Override
    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        /**
         * Test the format String
         */
        formatDateTime(new Date());
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int getBossEventLoopThreads() {
        return bossEventLoopThreads;
    }

    public void setBossEventLoopThreads(int bossEventLoopThreads) {
        this.bossEventLoopThreads = bossEventLoopThreads;
    }

    @Override
    public int getWorkerEventLoopThreads() {
        return workerEventLoopThreads;
    }

    public void setWorkerEventLoopThreads(int workerEventLoopThreads) {
        this.workerEventLoopThreads = workerEventLoopThreads;
    }

    public String formatDateTime(Date d) {
        return (new SimpleDateFormat(dateTimeFormat)).format(d);
    }

    public String formatTimestamp(Date d) {
        return (new SimpleDateFormat(timestamp)).format(d);
    }

    @Override
    public boolean isLogRequestResponse() {
        return logRequestResponse;
    }

    public void setLogRequestResponse(boolean logRequestResponse) {
        this.logRequestResponse = logRequestResponse;
    }

}
