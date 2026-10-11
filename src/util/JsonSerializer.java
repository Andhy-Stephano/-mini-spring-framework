package util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class JsonSerializer {

    /**
     * Convertit n'importe quel objet Java en chaine JSON.
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof List<?>) {
            return listToJson((List<?>) obj);
        }
        if (obj instanceof Map<?, ?>) {
            return mapToJson((Map<?, ?>) obj);
        }
        return objectToJson(obj);
    }

    /**
     * Convertit une List en tableau JSON : ["a","b","c"]
     */
    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Convertit une Map en objet JSON : {"cle":"valeur"}
     */
    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            sb.append(toJson(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convertit un objet Java en objet JSON via la reflexion.
     * Lit tous les attributs de la classe et les transforme en paires cle:valeur.
     */
    private static String objectToJson(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        // On parcourt tous les attributs declares dans la classe
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // permet de lire les attributs prives
            try {
                if (!first) sb.append(",");
                sb.append("\"").append(field.getName()).append("\":");
                sb.append(toJson(field.get(obj)));
                first = false;
            } catch (IllegalAccessException e) {
                // on ignore les attributs inaccessibles
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Echappe les caracteres speciaux dans une chaine JSON.
     */
    private static String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}