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

//This class can be called to get a String[] containing all open window titles. It implements a CustomClassLoader that allows us to unload the cLib.dll and delete it before shutdown.
public class EnumAllWindowNames {

    private static boolean loadedLibrary = false;
    private static CustomClassLoader cl = new CustomClassLoader();
    private static Class cls;
    private static CLibraryInterface ldl;

    //This is what is actually called to get the String[] of all open window titles.
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

    //Copies the cLib.dll packaged with the JAR and puts into the SuperToolkit folder. All dlls must be outside of the JAR to be loaded.
    private static void copyJNILibrary() {

        File fileOut = new File(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\SuperToolkit\\cLib.dll");
        fileOut.getParentFile().mkdirs();

        try (InputStream in = main.class.getResourceAsStream("/app/JNI/cLib.dll");
                FileOutputStream out = new FileOutputStream(fileOut)) {

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

    //The thread that will be run at the time of shutdown that will delete the cLib.dll if it was copied to SuperToolkit
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

    //This is borrowed completely from someone else. I'm not going to pretend that I understand even half of it.
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
