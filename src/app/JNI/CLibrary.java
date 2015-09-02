
package app.JNI;

//This class will load the cLib.dll that adds the ability to get a list of all open window names. It also contains the method that calls the cLib.dll code.
public class CLibrary implements CLibraryInterface {

    //The dll will be loaded the first time CLibrary is referenced. I will only be loaded once.
    static {
        System.load(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");
    }

    //The native keyword directs that the function is contained within a dll file.
    @Override
    public native String[] enumWindows();
}
