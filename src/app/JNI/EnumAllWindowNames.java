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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static CustomClassLoader cl = new CustomClassLoader();
    private static Class cls;
    private static CLibraryInterface ldl;

    public static String[] getWindowTitles() {

        if (!loadedLibrary) {

            System.out.println("cLib is not loaded. Loading it now.");

            try {

                copyJNILibrary();
                loadedLibrary = true;

                cls = cl.findClass("app.JNI.CLibrary");
                ldl = (CLibraryInterface) cls.newInstance();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                
                Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
                
            }

        }

        String[] names;

        try {

            names = ldl.enumWindows();

        } catch (NullPointerException e) {

            System.out.println("An error occured while tring to enumWindows");
            names = null;

        }

        return names;

    }

    private static void copyJNILibrary() {
        try {
            File fileOut = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");

            fileOut.getParentFile().mkdirs();

            InputStream in = main.class.getResourceAsStream("/app/JNI/cLib.dll");
            FileOutputStream out = new FileOutputStream(fileOut);

            int i;

            byte[] buffer = new byte[1024];

            while ((i = in.read(buffer)) > -1) {
                out.write(buffer, 0, i);
            }

            out.close();
            in.close();

            Runtime.getRuntime().addShutdownHook(new RemoveLibraryHook());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EnumAllWindowNames.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static class RemoveLibraryHook extends Thread {

        @Override
        @SuppressWarnings("empty-statement")
        public void run() {

            ldl = null;
            cls = null;
            cl = null;

            System.gc();

            File dir = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit");
            File file = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");

            while (!file.delete());
            while (!dir.delete());
            
            System.out.println("Loaded cLib.dll and containing folder have been deleted");
            
        }

    }

    public static class CustomClassLoader extends ClassLoader {

        /**
         * The HashMap where the classes will be cached
         */
        private Map<String, Class<?>> classes = new HashMap<>();

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
