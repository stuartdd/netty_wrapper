package server.logging;

import interfaces.LoggerConfig;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author stuartdd
 */
public class SimpleLoggingConfig implements LoggerConfig {

    private String logFileName = "nettyServer_%{ts}.log";
    private String timeStamp = "yyyy_MM_dd_HH_mm_ss";
    private boolean echoToConsole = false;

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean isEchoToConsole() {
        return echoToConsole;
    }

    public void setEchoToConsole(boolean echoToConsole) {
        this.echoToConsole = echoToConsole;
    }

    public String timeStampFormattedAsString() {
        return (new SimpleDateFormat(getTimeStamp())).format(new Date());
    }
}
