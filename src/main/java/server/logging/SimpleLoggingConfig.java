package server.logging;

import interfaces.LoggerConfig;
/**
 *
 * @author stuartdd
 */
public class SimpleLoggingConfig implements LoggerConfig {

    private String logFileName = "nettyServer_%{ts}.log";
    private String timeStampFileName = "yyyy_MM_dd";
    private String timeStampLogLine = "yyyy_MM_dd HH_mm_ss";
    private boolean echoToConsole = false;

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public String getTimeStampFileName() {
        return timeStampFileName;
    }

    public void setTimeStampFileName(String timeStampFileName) {
        this.timeStampFileName = timeStampFileName;
    }

    public String getTimeStampLogLine() {
        return timeStampLogLine;
    }

    public void setTimeStampLogLine(String timeStampLogLine) {
        this.timeStampLogLine = timeStampLogLine;
    }

    @Override
    public boolean isEchoToConsole() {
        return echoToConsole;
    }

    public void setEchoToConsole(boolean echoToConsole) {
        this.echoToConsole = echoToConsole;
    }
}
