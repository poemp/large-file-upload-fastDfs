package org.poem.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class URLUtil {
    /**
     * get class file path
     * @param clazz
     * @return
     */
    public static File getClassFile(Class clazz) {
        URL path = clazz.getResource(clazz.getName().substring(
                clazz.getName().lastIndexOf(".") + 1)
                + ".class");
        if (path == null) {
            String name = clazz.getName().replaceAll("[.]", "/");
            path = clazz.getResource("/" + name + ".class");
        }
        return new File(path.getFile());
    }


    /**
     *
     * get class classpath
     * @param clazz
     * @return
     */
    public static File getClassPathFile(Class clazz) {
        File file = getClassFile(clazz);
        for (int i = 0, count = clazz.getName().split("[.]").length; i < count; i++)
            file = file.getParentFile();
        if (file.getName().toUpperCase().endsWith(".JAR!")) {
            file = file.getParentFile();
        }
        return file;
    }

    /**
     * chinese char will error
     * @param clazz
     * @return
     */
    public static String getClassPath(Class clazz) {
        try {
            return java.net.URLDecoder.decode(getClassPathFile(clazz)
                    .getAbsolutePath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
