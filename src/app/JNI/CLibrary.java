/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.JNI;

/**
 *
 * @author Sloan
 */
public class CLibrary implements CLibraryInterface {

    static {
        System.load(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");
    }

    @Override
    public native String[] enumWindows();

}
