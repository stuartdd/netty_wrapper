package interfaces;

/**
 * To configure the logger you need these three methods
 * 
 * @author stuartdd
 */
public interface LoggerConfig {

    String getLogFileName();

    boolean isEchoToConsole();

    String timeStampFormattedAsString();

}
