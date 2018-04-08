package interfaces;

import server.HttpNettyRequest;
import server.HttpNettyResponse;

/**
 * Each handler needs to implement this method
 * 
 * @author stuartdd
 */
public interface HttpHandler {
    void handle(HttpNettyRequest request, HttpNettyResponse response, Logger logger);
}
