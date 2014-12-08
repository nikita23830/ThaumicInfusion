package drunkmafia.thaumicinfusion.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by DrunkMafia on 09/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ClassLoading {

    public static Class[] getClassFromPackage(String packageName){
        ArrayList<String> classNames = getClassNamesFromPackage(packageName);
        Class[] classes = new Class[classNames.size()];
        for(int i = 0; i < classes.length; i++){
            try{
                classes[i] = Class.forName(classNames.get(i));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return classes;
    }

    public static ArrayList<String> getClassNamesFromPackage(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<String>();;

        String path = packageName.replace(".", "/");
        packageURL = classLoader.getResource(path);
        try {
            if (packageURL.getProtocol().equals("jar")) {
                String jarFileName;
                JarFile jf;
                Enumeration<JarEntry> jarEntries;
                String entryName;

                // build jar file name, then loop through zipped entries
                jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
                jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
                System.out.println(">" + jarFileName);
                jf = new JarFile(jarFileName);
                jarEntries = jf.entries();
                while (jarEntries.hasMoreElements()) {
                    entryName = jarEntries.nextElement().getName();
                    if (entryName.startsWith(path) && entryName.length() > path.length() + 5) {
                        entryName = entryName.substring(path.length(), entryName.lastIndexOf('.'));
                        names.add(packageName + "." + entryName);
                    }
                }

                // loop through files in classpath
            } else {
                URI uri = new URI(packageURL.toString());
                File folder = new File(uri.getPath());
                // won't work with path which contains blank (%20)
                // File folder = new File(packageURL.getFile());
                File[] contenuti = folder.listFiles();
                String entryName;
                for (File actual : contenuti) {
                    entryName = actual.getName();
                    entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                    names.add(packageName + "." + entryName);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return names;
    }
}
