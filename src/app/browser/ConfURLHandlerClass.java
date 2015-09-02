package app.browser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

//This class is used to redirect any links using the conf protocol. We redirect to the operating system so Skype For Business can be called to open the conference call
public class ConfURLHandlerClass extends URLStreamHandler{

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        //The way Java sends commands to the command prompt is strange, so we have to ask for a new command prompt and execute the start command with it.
        Runtime.getRuntime().exec("cmd.exe /c start " + url.toExternalForm());
        //Once the command has called we don't need to diasplay anything so we load a blank page.
        return new URL("about:blank").openConnection();
    }
    
}
