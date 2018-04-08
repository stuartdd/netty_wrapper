package interfaces;

/**
 * If you wand to make a logger you need to implement these methods.
 *
 * For Example:  <code>
 * logger = new Logger() {
 *   String timeStamp = "yyyy_MM_dd_HH_mm_ss";
 *
 *   @Override
 *   public void init(LoggerConfig myNettyConfig) {
 *   }
 *
 *   @Override
 *   public void log(String message) {
 *       System.out.println(new SimpleDateFormat(timeStamp).format(new Date()) + ": " + message);
 *   }
 *
 *   @Override
 *   public void log(String message, Throwable ex) {
 *       System.out.println(new SimpleDateFormat(timeStamp).format(new Date()) + ": " + message + NL + SimpleLoggerImpl.toStringException(ex));
 *   }
 * };
 * </code>
 * @author stuartdd
 */
public interface Logger {

    void init(LoggerConfig myNettyConfig);

    void log(String message);

    void log(String message, Throwable ex);

}
