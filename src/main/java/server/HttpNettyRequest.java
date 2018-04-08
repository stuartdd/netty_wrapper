package server;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains (almost) every thing you could possible need for your
 * handler to do it's job!
 * 
 * This is generated from the netty request objects in HttpNettyServerHandler.
 * 
 * The idea is to provide easy access to the http request data
 * 
 * @author stuartdd
 * 
 */
public class HttpNettyRequest {

    private static final String NL = System.getProperty("line.separator");
    private String protocolVersion;
    private String uri;
    private String method;
    private String path;
    private String[] pathSegments;
    private final StringBuilder body = new StringBuilder();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path.startsWith("/")) {
            this.pathSegments = path.substring(1).split("/");
        } else {
            this.pathSegments = path.split("/");
        }
        this.path = path;
    }

    public String[] getPathSegments() {
        return pathSegments;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public HttpNettyRequest addHeader(CharSequence key, CharSequence value) {
        headers.put(key.toString(), value.toString());
        return this;
    }

    public HttpNettyRequest addParam(CharSequence key, CharSequence value) {
        parameters.put(key.toString(), value.toString());
        return this;
    }

    public String getBody() {
        return body.toString();
    }

    public HttpNettyRequest appendToBody(Object o) {
        if (o != null) {
            this.body.append(o.toString());
        }
        return this;
    }

    public boolean isLogging() {
        String noLog = parameters.get("noLog");
        return (noLog == null ? true : noLog.equalsIgnoreCase("false"));
    }

    @Override
    public String toString() {
        return "HttpNettyRequest[" + NL
                + "path[" + uri + "]" + NL
                + "pathSegments[" + arrayToString(pathSegments) + "]" + NL
                + "uri[" + uri + "]" + NL
                + "method[" + method + "]" + NL
                + "protocolVersion[" + protocolVersion + "]" + NL
                + "body[" + body + "]" + NL
                + "headers[" + mapToString(headers) + "]" + NL
                + "parameters[" + mapToString(parameters) + "]" + NL
                + "]";
    }

    public String toStringSmall() {
        String b = (body.length() == 0 ? "" : " body[" + body + "]");
        return "REQ :" + " uri[" + uri + "]" + " method[" + method + "]" + b;
    }

    private StringBuilder mapToString(Map<String, String> map) {
        StringBuilder str = new StringBuilder();
        int mark = str.length();
        for (Map.Entry<String, String> e : map.entrySet()) {
            str.append(e.getKey()).append('=').append(e.getValue());
            mark = str.length();
            str.append(NL);
        }
        str.setLength(mark);
        return str;
    }

    private StringBuilder arrayToString(String[] list) {
        StringBuilder str = new StringBuilder();
        int mark = str.length();
        for (String s : list) {
            str.append(s);
            mark = str.length();
            str.append('|');
        }
        str.setLength(mark);
        return str;
    }

}
