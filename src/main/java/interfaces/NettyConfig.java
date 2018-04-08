package interfaces;

/**
 * To configure the logger you need these methods
 *
 * @author stuartdd
 */
public interface NettyConfig {

    int getPort();

    boolean isLogRequestResponse();

    long getStartupDelayMs();

    int getWorkerEventLoopThreads();

    int getBossEventLoopThreads();

    boolean isSsl();

}
