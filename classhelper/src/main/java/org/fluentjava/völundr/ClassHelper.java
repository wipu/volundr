package org.fluentjava.völundr;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Improved version of https://dzone.com/articles/get-all-classes-within-package
 */
public final class ClassHelper {
    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and sub packages.
     *
     * @param packageName
     *            The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class<?>[] getClasses(final String packageName)
            throws IOException, ClassNotFoundException {
        final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources = classLoader.getResources(path);
        final List<Path> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            final File f = new File(
                    URLDecoder.decode(resource.getPath(), "UTF-8"));
            dirs.add(Paths.get(f.getAbsolutePath()));
        }
        final List<Class<?>> classes = new ArrayList<>();
        for (final Path directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(final Path path,
            final String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        if (!Files.exists(path)) {
            return classes;
        }
        File directory = path.toFile();
        final File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
        for (final File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                Path p = Paths.get(file.getAbsolutePath());
                classes.addAll(
                        findClasses(p, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName()
                        .substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
