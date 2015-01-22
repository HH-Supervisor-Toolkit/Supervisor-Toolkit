/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.JNI;

import app.main;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haywoosd
 */
public class EnumAllWindowNames {

    private static boolean loadedLibrary = false;
    private static CustomClassLoader cl = new CustomClassLoader() {
    };
    private static Class cls;
    private static LoadLibraryI ldl;

    public static String[] getWindowTitles() {
        try {
            
            if (!loadedLibrary) {
                
                copyJNILibrary();
                loadedLibrary = true;

                cls = cl.findClass("app.JNI.LoadLibrary");
                ldl = (LoadLibraryI) cls.newInstance();
                
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ldl.enumWindows();
        
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

    private static class RemoveLibraryHook extends Thread {

        @Override
        public void run() {

            cl = null;
            cls = null;
            ldl = null;
            System.gc();
            
            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            File dir = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit");
            File file = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");

            file.delete();
            dir.delete();

        }

    }

    public static class CustomClassLoader extends ClassLoader {

    /**
     * The HashMap where the classes will be cached
     */
    private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

    @Override
    public String toString() {
        return CustomClassLoader.class.getName();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (classes.containsKey(name)) {
            return classes.get(name);
        }

        byte[] classData;

        try {
            classData = loadClassData(name);
        } catch (IOException e) {
            throw new ClassNotFoundException("Class [" + name
                    + "] could not be found", e);
        }

        Class<?> c = defineClass(name, classData, 0, classData.length);
        resolveClass(c);
        classes.put(name, c);

        return c;
    }

    /**
     * Load the class file into byte array
     * 
     * @param name
     *            The name of the class e.g. com.codeslices.test.TestClass}
     * @return The class file as byte array
     * @throws IOException
     */
    private byte[] loadClassData(String name) throws IOException {
        BufferedInputStream in = new BufferedInputStream(
                ClassLoader.getSystemResourceAsStream(name.replace(".", "/")
                        + ".class"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i;

        while ((i = in.read()) != -1) {
            out.write(i);
        }

        in.close();
        byte[] classData = out.toByteArray();
        out.close();

        return classData;
    }

}
    
}
