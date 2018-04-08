package server.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import interfaces.Logger;
import interfaces.LoggerConfig;
import java.io.FileNotFoundException;

/**
 *
 * @author stuartdd
 */
public class SimpleLoggerImpl implements Logger {

    public static final String TIME_STAMP_TAG = "%{ts}";
    private static FileOutputStream logOutputStream;
    private static final String NL = System.getProperty("line.separator");
    private static LoggerConfig loggerConfig;
    private static String finalFileName;

    @Override
    public void init(LoggerConfig myLoggerConfig) {

        if (myLoggerConfig == null) {
            throw new LoggingException("LoggerConfig cannot be null for SimpleLogger");
        }

        loggerConfig = myLoggerConfig;
        finalFileName = loggerConfig.getLogFileName();
        int pos = finalFileName.indexOf(TIME_STAMP_TAG);
        if (pos >= 0) {
            finalFileName = finalFileName.substring(0, pos) + loggerConfig.timeStampFormattedAsString() + finalFileName.substring(pos + TIME_STAMP_TAG.length());
        }
        File f = new File(finalFileName);
        finalFileName = f.getAbsolutePath();
        
        f = new File(f.getAbsolutePath());
        if (loggerConfig.isEchoToConsole()) {
            System.out.println("FINAL Log File Name is :" + finalFileName);
        }

        try {
            logOutputStream = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            throw new LoggingException("Could not create logFile " + finalFileName, ex);
        }
        writeLog("Log created!");
    }

    @Override
    public void log(String message) {
        log(message, null);
    }

    @Override
    public void log(String message, Throwable ex) {
        if (ex == null) {
            if (loggerConfig.isEchoToConsole()) {
                System.out.println(message);
            }
            writeLog(message);
        } else {
            writeLog(message + NL + toStringException(ex));
        }
    }

    private void writeLog(String message) {
        if (message == null) {
            message = null;
        }
        if (logOutputStream != null) {
            try {
                logOutputStream.write((loggerConfig.timeStampFormattedAsString() + ":" + message + NL).getBytes(Charset.defaultCharset()));
            } catch (Exception io) {
                System.err.println("Failed to write to log file:" + message + ":EX:" + io.getMessage());
            }
        }
    }

    public static String toStringException(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    public String getFinalLogName() {
        return finalFileName;
    }

}
