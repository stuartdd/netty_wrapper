package server;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import templating.Template;

/**
 * Contains (almost) every thing you could possible need for your handler to do
 * it's job!
 *
 * In your handler you append the response text and return. The other parameters
 * can be left.
 *
 * You may need to set the status and ContentType if the defaults are not
 * applicable
 *
 * See the examples in the test package
 *
 * @author stuartdd
 *
 */
public class HttpNettyResponse {

    private static final int MAX_LOG_LENGTH = 100;
    private boolean keepAlive;
    private boolean complete;
    private boolean error;
    private final StringBuilder buffer = new StringBuilder();
    private byte[] byteBuffer;
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private final HttpHeaders headers = new DefaultHttpHeaders();

    public HttpNettyResponse() {
        this.complete = false;
        this.error = false;
        setContentType("text/plain; charset=UTF-8");
    }

    /**
     * Load a file from a resource directory in a root path.
     *
     * The file is loaded and returned to the browser as a byte stream.
     *
     * This can be used to load binary media files as well as html, css, xml,
     * json and any other file type.
     *
     * The Mimi Type for the file is defined by the file extension (.gif, .jpg
     * etc) using the MimeTypes class.
     *
     * If the file is not found. The following message is returned to the browser. With a status of 404.
     * 
     * {"Status":404, "Msg":"Not Found", "Entity":"2018-04-12T12:12:42.589:Resource 'index.html' was not found"}
     * 
     * If the mime type cannot be derived an exception is thrown.
     *
     * If the mime type identifies the file as a text file (html, css, ,xml etc)
     * then the parameters are used to update the file using the Template class.
     *
     * This enables you to tailor a static response with name value pairs.
     *
     * See the Template class for substitution details.
     *
     * @param root The root file system. For example /home/myserver
     * @param resource The resource name. For example images, css, data
     * @param file The file name. For example file.json, index.html, my.css, image.gif.
     * @param parameters
     */
    public void loadMedia(String root, String resource, String file, Map<String, String> parameters) {
        File f;
        if (resource == null) {
            if (root == null) {
                f = new File(file);
            } else {
                f = new File(root + File.separator + file);
            }
        } else {
            if (root == null) {
                f = new File(resource + File.separator + file);
            } else {
                f = new File(root + File.separator + resource + File.separator + file);
            }
        }
        if (!f.exists()) {
            throw new ServerGeneralException(HttpResponseStatus.NOT_FOUND, "Resource '" + file + "' was not found");
        }

        String contentType = MimeTypes.getMimeType(file);
        setContentType(contentType);
        buffer.setLength(0);
        try {
            byteBuffer = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        } catch (IOException ex) {
            throw new ServerGeneralException(HttpResponseStatus.EXPECTATION_FAILED, "Resource '" + file + "' could not be loaded", ex);
        }
        if ((MimeTypes.isTextType(contentType)) && (parameters != null)) {
            buffer.append(Template.parse(new String(byteBuffer, Charset.defaultCharset()), parameters));
            byteBuffer = null;
        }
        setComplete();
    }

    public void setError(HttpResponseStatus status, String text) {
        this.status = status;
        this.error = true;
        buffer.setLength(0);
        buffer.append(text);
        complete = true;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setComplete() {
        complete = true;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public String getContentType() {
        return headers.get(HttpHeaderNames.CONTENT_TYPE).toString();
    }

    public final void setContentType(String contentType) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    public String getBuffer() {
        return buffer.toString();
    }

    public boolean isByteData() {
        return ((byteBuffer != null) && (byteBuffer.length > 0));
    }

    public boolean isError() {
        return error;
    }

    public byte[] getByteBuffer() {
        return byteBuffer;
    }

    public HttpNettyResponse setByteBuffer(byte[] bytes, String contentType) {
        byteBuffer = bytes;
        this.setContentType(contentType);
        this.status = HttpResponseStatus.OK;
        buffer.setLength(0);
        complete = true;
        return this;
    }

    public HttpNettyResponse clear() {
        complete = false;
        buffer.setLength(0);
        return this;
    }

    public HttpNettyResponse append(Object o) {
        if (!complete) {
            if (o != null) {
                buffer.append(o.toString());
            }
        }
        return this;
    }

    @Override
    public String toString() {
        if (isError()) {
            return "{\"Status\":" + getStatus().code() + ", \"Msg\":\"" + getStatus().reasonPhrase() + "\", \"Entity\":\"" + getBuffer() + "\"}";
        }
        return getBuffer();
    }

    String toStringSmall() {
        if ((byteBuffer != null) && (byteBuffer.length > 0)) {
            return "RESP: " + getContentType() + " Length[" + byteBuffer.length + "]";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : toString().toCharArray()) {
            if (c >= ' ') {
                sb.append(c);
            } else {
                sb.append('\\').append('n');
            }
            if (sb.length() > MAX_LOG_LENGTH) {
                break;
            }
        }
        return "RESP: " + getContentType() + " [" + sb.toString() + "]";
    }

}
