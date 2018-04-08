package logger;

import interfaces.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;
import server.logging.SimpleLoggingConfig;
import server.logging.LoggingException;
import server.logging.SimpleLoggerImpl;

/**
 *
 * @author stuartdd
 */
public class TestLogger {

    private static final String EXCEPTION_MESSAGE_HEAD = "LOGGING-EXCEPTION-MESSAGE-HEAD";
    private static final String EXCEPTION_MESSAGE_REASON = "LOGGING-EXCEPTION-MESSAGE-REASON";
    private static final String LOG_FILE_PREF = "timeStamp-";
    private static final String LOG_FILE_POST = "-pleaseDeleteMe.log";
    private static final String LOG_FILE_NAME = LOG_FILE_PREF + SimpleLoggerImpl.TIME_STAMP_TAG + LOG_FILE_POST;
    private static final String LOG_MESSAGE = "1234567890";

    private SimpleLoggingConfig loggingConfig = new SimpleLoggingConfig();

    @Test()
    public void testSimpleLoggerFunctions() throws IOException {
        SimpleLoggingConfig slc = new SimpleLoggingConfig();
        slc.setLogFileName(LOG_FILE_NAME);
        File f = null;
        try {
            SimpleLoggerImpl logger = new SimpleLoggerImpl();
            logger.init(slc);
            f = new File(logger.getFinalLogName());
            Assert.assertTrue("Log file not created", f.exists());
            logger.log(LOG_MESSAGE);
            String s = new String(Files.readAllBytes(FileSystems.getDefault().getPath(logger.getFinalLogName())));
            String fln = logger.getFinalLogName();
            Assert.assertTrue("Log file does not contain '" + LOG_MESSAGE + "'", s.contains(LOG_MESSAGE));
            Assert.assertTrue("Log file name should end in '" + LOG_FILE_POST + "'", fln.endsWith(LOG_FILE_POST));
            Assert.assertTrue("Log file name should contain '" + LOG_FILE_PREF + "'", fln.contains(LOG_FILE_PREF));
            Assert.assertTrue("Log file name should have expanded the time stamp'" + LOG_FILE_PREF + "'", fln.length() > (LOG_FILE_NAME.length() + 5));

            logger.log(EXCEPTION_MESSAGE_HEAD, new LoggingException(EXCEPTION_MESSAGE_REASON));
            String s2 = new String(Files.readAllBytes(FileSystems.getDefault().getPath(logger.getFinalLogName())));
            Assert.assertTrue("Log file does not contain '" + LoggingException.class.getName() + "'", s2.contains(LoggingException.class.getName()));
            Assert.assertTrue("Log file does not contain '" + EXCEPTION_MESSAGE_HEAD+ "'", s2.contains(EXCEPTION_MESSAGE_HEAD));
            Assert.assertTrue("Log file does not contain '" + EXCEPTION_MESSAGE_REASON+ "'", s2.contains(EXCEPTION_MESSAGE_REASON));
            Assert.assertTrue("Log file does not contain 'at "+this.getClass().getName()+"'", s2.contains("at "+this.getClass().getName()));
        } finally {
            if (f != null) {
                Assert.assertTrue("Log file not created", f.exists());
                f.delete();
                Assert.assertFalse("Log file not deleted", f.exists());
            }
        }
    }

    @Test(expected = LoggingException.class)
    public void testLoggerWithNoNettyConfig() {
        Logger logger = new SimpleLoggerImpl();
        logger.init(null);
    }

    @Test(expected = LoggingException.class)
    public void testLoggerWithInvalidFileName() {
        loggingConfig.setLogFileName("/root/notPossible.log");
        Logger logger = new SimpleLoggerImpl();
        logger.init(loggingConfig);
    }

}
