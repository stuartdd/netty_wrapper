package server;

import interfaces.HttpHandler;
import interfaces.Logger;
import io.netty.handler.codec.http.HttpResponseStatus;
import server.router.Router;

/**
 * Forward HttpNettyRequest to a handler.
 *
 * Find the handler and call the handle method on the first match
 *
 * Invoke the handler
 *
 *
 * @author stuartdd
 *
 */
public class DispatcherImpl implements interfaces.Dispatcher {

    private final Router router;

    /**
     * Netty requests are all handled by this dispatcher.
     *
     * Requests that match a route will have a handler.
     *
     * The handler is called with the request response pair.
     *
     * Requests that do not match the route will throw an exception. This is
     * handles in the HttpNettyServerHandler.
     *
     */
    public DispatcherImpl() {
        this.router = new Router();
    }

    /**
     * Add a route. This is a path that matches the request path. See the Router
     * for details.
     *
     * @param path a list of path elements defining the route
     * @param handler a handler to be called if the route matches
     */
    @Override
    public void addRoute(String[] path, HttpHandler handler) {
        router.add(path, handler);
    }

    /**
     * Called by the HttpNettyServerHandler each time a request is received.
     * 
     * @param request The request details
     * @param response The response details
     * @param logger Pass the logger in so the handler can log stuff.
     */
    @Override
    public void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger) {
        HttpHandler handler = (HttpHandler) router.match(request.getPathSegments());
        if (handler == null) {
            response.setError(HttpResponseStatus.BAD_REQUEST, "Route [" + arrayToString(request.getPathSegments(), "|") + "] not recognised");
        } else {
            handler.handle(request, response, logger);
        }
    }

    /**
     * Used for error handling only.
     *
     * @param list list of path elements (segments)
     * @param delim the separator to use.
     * @return The list as a String with delimeters
     */
    private static String arrayToString(Object[] list, String delim) {
        StringBuilder sb = new StringBuilder();
        int mark = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {
                sb.append(list[i].toString());
            } else {
                sb.append("null");
            }
            mark = sb.length();
            sb.append(delim);
        }
        sb.setLength(mark);
        return sb.toString();
    }

}
