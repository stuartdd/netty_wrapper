package server;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author stuar
 */
public class ServerGeneralException extends RuntimeException {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
    private HttpResponseStatus status;

    public ServerGeneralException(HttpResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ServerGeneralException(HttpResponseStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return getTimestamp() + ":" + super.getMessage(); //To change body of generated methods, choose Tools | Templates.
    }

    private String getTimestamp() {
        return sdf.format(new Date());
    }
}
