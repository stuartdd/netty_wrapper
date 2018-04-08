/*
 * To change this template, choose Tools | Templates
 * BT eCommerce 
 */
package templating;

/**
 * Class description
 *
 * @version $Rev: $ $Date: $
 */
public class TemplateException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 4901505388413951424L;

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(String message, Exception ex) {
        super(message, ex);
    }

}
