package interfaces;

import server.HttpNettyRequest;
import server.HttpNettyResponse;

/**
 * This is what a Dispatcher needs to implement.
 *
 * @author stuartdd
 */
public interface Dispatcher {

    /**
     * Add a route. This is a path that matches the request path. See the Router
     * for details.
     *
     * @param path a list of path elements defining the route
     * @param handler a handler to be called if the route matches
     */
    void addRoute(String[] path, HttpHandler handler);

    /**
     * Called by the HttpNettyServerHandler each time a request is received.
     *
     * Match a route and call the required handler
     * 
     * @param request The request details
     * @param response The response details
     * @param logger Pass the logger in so the handler can log stuff.
     */
    void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger);
}
