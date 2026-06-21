package util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Recupere toutes les classes presentes dans un package donne.
     * Exemple : getClasses("test") va chercher dans le dossier "test"
     * et retourner la liste de toutes les classes qu'il contient.
     */
    public static List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        // On transforme "test" en chemin de dossier "test"
        String path = packageName.replace('.', '/');

        // Le ClassLoader sait ou se trouvent les fichiers .class sur le disque
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            System.out.println("[Framework] Package introuvable : " + packageName);
            return classes;
        }

        // On transforme l'URL en vrai dossier qu'on peut fouiller
        File directory = new File(resource.getFile());

        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    // On ne garde que les fichiers compiles .class
                    if (file.getName().endsWith(".class")) {
                        String nomClasseSansExtension = file.getName().replace(".class", "");
                        String nomClasseComplet = packageName + "." + nomClasseSansExtension;

                        // On charge reellement la classe a partir de son nom
                        classes.add(Class.forName(nomClasseComplet));
                    }
                }
            }
        }
        return classes;
    }

    /**
     * Version generale : parcourt tout le classpath (sera completee plus tard si besoin).
     */
    public static List<Class<?>> getAllClassesFromClasspath() throws Exception {
        return new ArrayList<>();
    }
}