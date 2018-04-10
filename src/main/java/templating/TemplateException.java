package templating;

/**
 * @Author stuartdd
 */
public class TemplateException extends Exception {

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(String message, Exception ex) {
        super(message, ex);
    }

}
