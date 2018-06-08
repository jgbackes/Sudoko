package com.backesfamily.soduku;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ReflectionHelper {

    public static ArrayList<Class<?>> findClassesImplementing(final Class<?> interfaceClass, final Package fromPackage) {

        if (interfaceClass == null) {
            debug("Unknown subclass.");
            return null;
        }

        if (fromPackage == null) {
            debug("Unknown package.");
            return null;
        }

        final ArrayList<Class<?>> rVal = new ArrayList<>();
        try {
            final Class<?>[] targets = getAllClassesFromPackage(fromPackage.getName());
            for (Class<?> aTarget : targets) {
                if (aTarget == null) {
                    debug("aTarget was null");
                } else if (aTarget.equals(interfaceClass)) {
                    debug("Found the interface definition.");
                } else if (!interfaceClass.isAssignableFrom(aTarget)) {
                    debug("Class '" + aTarget.getName() + "' is not a " + interfaceClass.getName());
                } else {
                    rVal.add(aTarget);
                }
            }
        } catch (ClassNotFoundException e) {
            debug("Error reading package name.");
            debug(e);
        } catch (IOException e) {
            debug("Error reading classes in package.");
            debug(e);
        }

        return rVal;
    }

    /**
     * Load all classes from a package.
     *
     * @param packageName Name of the package to search
     * @return An array of call of the classes in the given package
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getAllClassesFromPackage(final String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Find file in package.
     *
     * @param directory Directory to search
     * @param packageName Package to search
     * @return An List of classes
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    private static void debug(String message) {
        System.out.println(message);
    }

    private static void debug(Exception e) {
        e.printStackTrace();
    }
}