
package app.JNI;

//This interface is required so that CLibrary can extend it. By having CLibrary extend CLibraryInterface we can cast to it from the new instance of the CLibrary 
//created from the CustomClassLoader in EnumAllWindowNames.
public interface CLibraryInterface {
    public abstract String[] enumWindows();
}
