/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import interfaces.Logger;

/**
 * For a class to be loggable it need to implement this interface.
 * 
 * Not a lot of use to the developer but was required by:
 * 
 * HttpNettyServerHandler and HttpNettyServerInitializer
 * 
 * @author stuart
 */
public interface Loggable {
    Logger getLogger();
}
