/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.JNI;

import app.main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haywoosd
 */
public class EnumAllWindowNames {

    private static native String[] enumWindows();
    private static boolean loadedLibrary = false;

    public static String[] getWindowTitles() {
        if (!loadedLibrary) {
            copyJNILibrary();
            loadedLibrary = true;
            System.load(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");
        }

        return enumWindows();

    }

    private static void copyJNILibrary() {

        File dir = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File fileOut = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");

        try {
            InputStream in = main.class.getResourceAsStream("/app/JNI/cLib.dll");
            OutputStream out = new FileOutputStream(fileOut);

            int i;
            byte[] buffer = new byte[1024];

            while ((i = in.read(buffer)) > -1) {
                out.write(buffer, 0, i);
            }
            
            out.close();
            in.close();
            
        } catch (IOException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Runtime.getRuntime().addShutdownHook(new RemoveLibraryHook());
        
    }
    
    private static class RemoveLibraryHook extends Thread{
        
        @Override
        public void run(){
                        
            System.out.println("Test Test Test");
            
            File dir = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit");
            File file = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");
                        
            file.delete();
            dir.delete();
            
        }
        
    }
}
