/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haywoosd
 */
public class ConfURLHandlerClass extends URLStreamHandler{

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        
        try {
            
            Desktop.getDesktop().browse(url.toURI());
            
        } catch (URISyntaxException ex) {
            
            Logger.getLogger(ConfURLHandlerClass.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        return new URL("about:blank").openConnection();
    }
    
}
