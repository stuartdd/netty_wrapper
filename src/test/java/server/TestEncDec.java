/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import junit.framework.Assert;
import org.junit.Test;
import server.DecodeEncode;

/**
 *
 * @author stuar
 */
public class TestEncDec {

    @Test
    public void testDec() {
        String enc = "A%20B%27C%40D%25E %2A %4B %4C %4D %4E %3F %29";
        String dec = "A B'C@D%E * K L M N ? )";
        
        Assert.assertEquals(dec, DecodeEncode.decode(enc));
    }
    
    @Test
    public void testEnc() {
        String enc = "St%27T%20You%20are%40%20percent%25";
        String dec = "St'T You are@ percent%";
        Assert.assertEquals(enc, DecodeEncode.encode(dec));
        Assert.assertEquals(dec, DecodeEncode.decode(enc));
    }
    
    
}
