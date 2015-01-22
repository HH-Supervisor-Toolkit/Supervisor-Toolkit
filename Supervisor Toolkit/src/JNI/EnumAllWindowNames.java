/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JNI;

import app.main;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author haywoosd
 */
public class EnumAllWindowNames {

    private static native String[] enumWindows();
    private static boolean loadedLibrary = false;
    
   public static String[] getWindowTitles(){
       if (!loadedLibrary){
           InputStream in = main.class.getResourceAsStream("/JNI/cLib.dll");
           File fileOut = new File (System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");
           OutputStream out = FileOutputStream(fileOut);
           
       }
   }
   
    
}
