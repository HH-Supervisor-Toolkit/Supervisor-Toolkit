/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.browser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *
 * @author haywoosd
 */
public class ConfURLHandlerClass extends URLStreamHandler{

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        
        Runtime.getRuntime().exec("cmd.exe /c start " + url.toExternalForm());
                 
        return new URL("about:blank").openConnection();
    }
    
}
