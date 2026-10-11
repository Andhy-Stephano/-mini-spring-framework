package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import annotation.Controller;
import annotation.GetMapping;
import annotation.HttpMethod;
import annotation.RequestParam;
import annotation.RestAPI;
import util.Utils;
import exception.BindingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.JsonSerializer;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> listeControllers = new ArrayList<>();
    private Map<String, Method> urlMapping = new HashMap<>();

    @Override
    public void init() throws ServletException {
        try {
            List<Class<?>> toutesLesClasses = Utils.getClasses("test");

            for (Class<?> classe : toutesLesClasses) {
                if (classe.isAnnotationPresent(Controller.class)) {
                    listeControllers.add(classe);
                    System.out.println("[Framework] Controleur detecte : " + classe.getName());
                }
            }
            // Pour chaque controleur trouve, on scanne ses methodes
            for (Class<?> controleur : listeControllers) {
                Method[] methodes = controleur.getDeclaredMethods();
                for (Method methode : methodes) {
                    if (methode.isAnnotationPresent(GetMapping.class)) {
                        GetMapping annotation = methode.getAnnotation(GetMapping.class);
                        String url = annotation.value();
                        HttpMethod[] methodesHttp = annotation.method();

                        // Pour chaque methode HTTP declaree, on cree une entree dans le carnet
                        for (HttpMethod methodHttp : methodesHttp) {
                           String cle = url + " " + methodHttp.name();;
                            urlMapping.put(cle, methode);
                            System.out.println("[Framework] URL enregistree : " + cle + " -> " + methode.getName());
                        }
                    }
                }
            }
            System.out.println("[Framework] Scan termine. " + listeControllers.size() + " controleur(s) reconnu(s).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // // Méthode centrale qui gère toutes les requêtes
    // protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {
        
    //     response.setContentType("text/html;charset=UTF-8");
    //     response.setCharacterEncoding("UTF-8");
    //     PrintWriter out = response.getWriter();
        
    //     // On récupère l'URL complète tapée par l'utilisateur
    //     String urlDemandee = request.getRequestURL().toString();
    //     String methodeHttp = request.getMethod();
        
    //     // On affiche le résultat directement sur l'écran du navigateur
    //     out.println("<html>");
    //     out.println("<head><title>Sprint 0</title></head>");
    //     out.println("<body>");
    //     out.println("<h1>[Sprint 0] Mon Framework Spring MVC</h1>");
    //     out.println("<p>Le FrontControllerServlet a bien intercepte la requete.</p>");
    //     out.println("<strong>L'URL detectee est :</strong> " + urlDemandee);
    //     out.println("<br><strong>Methode HTTP utilisee :</strong> " + methodeHttp);
    //    // out.println("<br><strong>Nombre de controleurs detectes :</strong> " + listeControllers.size());
    //     out.println("<br><strong>Controleurs detectes :</strong>");
    //     out.println("<ul>");
    //     for (Class<?> controleur : listeControllers) {
    //         out.println("<li>" + controleur.getSimpleName() + "</li>");
    //     }
    //     out.println("</ul>");
    //     out.println("</body>");
    //     out.println("</html>");
    // }

protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String contextPath = request.getContextPath();
    String urlComplete = request.getRequestURI();
    String url = urlComplete.substring(contextPath.length());
    String methodeHttp = request.getMethod().toUpperCase();
    String cle = url + " " + methodeHttp;

    if (urlMapping.containsKey(cle)) {
        Method methode = urlMapping.get(cle);

        // La methode a-t-elle l'annotation @RestAPI ?
        if (methode.isAnnotationPresent(RestAPI.class)) {

            // === MODE REST API : on retourne du JSON ===
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            try {
                // On cree une instance du controleur
                Object controleurInstance = methode.getDeclaringClass()
                                                   .getDeclaredConstructor()
                                                   .newInstance();

                // SPRINT 7 : on resout les arguments a partir de la requete
                Object[] arguments = resolveArguments(methode, request);

                // On appelle la methode et on recupere le resultat
                Object resultat = methode.invoke(controleurInstance, arguments);

                // On lit l'attribut alreadyJson de l'annotation
                RestAPI annotation = methode.getAnnotation(RestAPI.class);

                if (annotation.alreadyJson()) {
                    // Le resultat est deja du JSON -> afficher directement
                    out.print(resultat.toString());

                } else if (resultat instanceof String) {
                    // Le resultat est un String simple -> afficher tel quel
                    out.print(resultat.toString());

                } else {
                    // Le resultat est un objet Java -> convertir en JSON avec Gson
                   out.print(JsonSerializer.toJson(resultat));
                }

            } catch (BindingException e) {
                // Erreur de data binding -> reponse 400 avec le message
                response.setStatus(400);
                out.print("{\"erreur\": \"" + e.getMessage() + "\"}");
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(500);
                response.getWriter().print(
                    "{\"erreur\": \"Erreur interne : " + e.getMessage() + "\"}"
                );
            }

        } else {

            // === MODE NORMAL : comportement comme SPRINT2/3 ===
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            try {
                Object controleurInstance = methode.getDeclaringClass()
                                                   .getDeclaredConstructor()
                                                   .newInstance();
                Object[] arguments = resolveArguments(methode, request);
                Object resultat = methode.invoke(controleurInstance, arguments);

                out.println("<html><body>");
                out.println("<h1>[Sprint 7] Mon Framework Spring MVC</h1>");
                out.println("<p>URL : <strong>" + url + "</strong></p>");
                out.println("<p>Methode : <strong>" + methode.getName() + "</strong></p>");
                out.println("<p>Controleur : <strong>" +
                    methode.getDeclaringClass().getSimpleName() + "</strong></p>");
                if (resultat != null) {
                    out.println("<p>Resultat : <strong>" + resultat + "</strong></p>");
                }
                out.println("</body></html>");

            } catch (BindingException e) {
                // Erreur de data binding -> reponse 400 avec le message
                response.setStatus(400);
                out.println("<html><body>");
                out.println("<h1>BindingException</h1>");
                out.println("<p style=\"color:red\">" + e.getMessage() + "</p>");
                out.println("</body></html>");
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(500);
                out.println("<html><body><h1>Erreur interne</h1><p>"
                    + e.getMessage() + "</p></body></html>");
            }
        }

    } else {

        // === URL INCONNUE : retourner une erreur JSON ===
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"erreur\": \"URL inconnue\", \"urlsDisponibles\": [");
        boolean premier = true;
        for (String urlConnue : urlMapping.keySet()) {
            if (!premier) sb.append(", ");
            sb.append("\"").append(urlConnue).append("\"");
            premier = false;
        }
        sb.append("]}");
        out.print(sb.toString());
    }
    }

    // ============================================================
    // SPRINT 7 : DATA BINDING (liaison des parametres de la requete
    // aux parametres de la methode du controleur)
    // ============================================================

    /**
     * Construit le tableau d'arguments a passer a methode.invoke(...).
     * Pour chaque parametre de la methode :
     *  - si c'est un HttpServletRequest, on injecte la requete,
     *  - sinon on fait le binding par nom (@RequestParam ou nom du parametre).
     */
    private Object[] resolveArguments(Method methode, HttpServletRequest request)
            throws BindingException {
        Parameter[] parametres = methode.getParameters();
        Object[] arguments = new Object[parametres.length];

        // On transforme la Map<String, String[]> en Map<String, String>
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entree : request.getParameterMap().entrySet()) {
            String[] valeurs = entree.getValue();
            params.put(entree.getKey(),
                (valeurs != null && valeurs.length > 0) ? valeurs[valeurs.length - 1] : null);
        }

        for (int i = 0; i < parametres.length; i++) {
            Parameter parametre = parametres[i];
            if (HttpServletRequest.class.isAssignableFrom(parametre.getType())) {
                // Injection de la requete directement
                arguments[i] = request;
            } else if (isSimpleType(parametre.getType())) {
                // Type simple (String, int, double...) -> binding par nom
                arguments[i] = bind(params, parametre);
            } else {
                // Objet complexe (Employe, Personne...) -> SPRINT 7 BIS
                arguments[i] = bindObject(parametre.getType(), params);
            }
        }
        return arguments;
    }

    /**
     * Fait le binding d'UN parametre de type SIMPLE avec les valeurs de la requete.
     * Testable sans servlet (on passe simplement une Map).
     * Les objets complexes sont traites par bindObject (Sprint 7 bis).
     */
    public static Object bind(Map<String, String> params, Parameter parametre)
            throws BindingException {
        Class<?> type = parametre.getType();

        // Nom de la variable : @RequestParam("nom") sinon nom du parametre (-parameters)
        RequestParam requestParam = parametre.getAnnotation(RequestParam.class);
        String nom = (requestParam != null && !requestParam.value().isEmpty())
                ? requestParam.value()
                : parametre.getName();
        boolean obligatoire = (requestParam != null) && requestParam.required();

        String valeur = params.get(nom);
        if (valeur == null) {
            if (obligatoire) {
                throw new BindingException("Parametre obligatoire manquant : " + nom);
            }
            return defaultValue(type);
        }
        return convert(valeur, type);
    }

    /**
     * true si le type est simple (String, primitif ou wrapper).
     * false pour les objets complexes (Employe, List, ...).
     */
    public static boolean isSimpleType(Class<?> type) {
        return type == String.class
                || type.isPrimitive()
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == Float.class
                || type == Short.class
                || type == Byte.class
                || type == Boolean.class
                || type == Character.class;
    }

    /**
     * Valeur par defaut quand le parametre est absent :
     * primitif -> 0 / 0.0 / false / '\0', String et wrappers -> null.
     */
    public static Object defaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        return null;
    }

    /**
     * Convertit une chaine envoyee par le formulaire vers le type attendu.
     * Leve BindingException si le format est invalide.
     */
    public static Object convert(String valeur, Class<?> type) throws BindingException {
        try {
            if (type == String.class) return valeur;
            if (type == int.class || type == Integer.class) return Integer.parseInt(valeur.trim());
            if (type == long.class || type == Long.class) return Long.parseLong(valeur.trim());
            if (type == double.class || type == Double.class) return Double.parseDouble(valeur.trim());
            if (type == float.class || type == Float.class) return Float.parseFloat(valeur.trim());
            if (type == short.class || type == Short.class) return Short.parseShort(valeur.trim());
            if (type == byte.class || type == Byte.class) return Byte.parseByte(valeur.trim());
            if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(valeur.trim());
            if (type == char.class || type == Character.class) {
                String s = valeur.trim();
                if (s.length() != 1) {
                    throw new BindingException(
                        "Valeur invalide pour char, il faut 1 seul caractere : '" + valeur + "'");
                }
                return s.charAt(0);
            }
            return null;
        } catch (NumberFormatException e) {
            throw new BindingException(
                "Valeur invalide pour " + type.getSimpleName() + " : '" + valeur + "'");
        }
    }

    /**
     * SPRINT 7 BIS : lie les champs d'un formulaire a un OBJET complexe.
     * 1. instancie l'objet (constructeur sans argument obligatoire),
     * 2. parcourt ses champs (y compris la hierarchie, sans statistiques/synthetiques),
     * 3. pour chaque champ simple present dans la requete, convertit la valeur
     *    et l'injecte via le setter setXxx sinon par acces direct (reflexion).
     * Les champs non simples (objet imbrique, List...) sont ignores.
     */
    public static Object bindObject(Class<?> type, Map<String, String> params)
            throws BindingException {
        Object objet;
        try {
            objet = type.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new BindingException(
                "Constructeur sans argument manquant pour : " + type.getSimpleName());
        }

        for (Field champ : getAllFields(type)) {
            // On ignore les champs statiques et synthetiques
            if (Modifier.isStatic(champ.getModifiers()) || champ.isSynthetic()) {
                continue;
            }
            // Champ de type non simple (objet imbrique, List...) -> on ignore
            if (!isSimpleType(champ.getType())) {
                continue;
            }

            String valeur = params.get(champ.getName());
            if (valeur == null) {
                continue; // absent de la requete -> valeur par defaut conservee
            }

            Object convertir = convert(valeur, champ.getType());

            // 1er choix : le setter setXxx s'il existe
            if (!injecterParSetter(objet, champ, convertir)) {
                // 2eme choix : acces direct au champ via la reflexion
                try {
                    champ.setAccessible(true);
                    champ.set(objet, convertir);
                } catch (Exception e) {
                    throw new BindingException(
                        "Impossible d'injecter le champ " + champ.getName());
                }
            }
        }
        return objet;
    }

    /**
     * Raccourci lie a la requete HTTP (utilise par le Servlet).
     */
    public static Object bindObject(Class<?> type, HttpServletRequest request)
            throws BindingException {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entree : request.getParameterMap().entrySet()) {
            String[] valeurs = entree.getValue();
            params.put(entree.getKey(),
                (valeurs != null && valeurs.length > 0) ? valeurs[valeurs.length - 1] : null);
        }
        return bindObject(type, params);
    }

    /**
     * Recupere tous les champs declares dans la classe et ses super-classes.
     */
    private static List<Field> getAllFields(Class<?> classe) {
        List<Field> champs = new ArrayList<>();
        Class<?> c = classe;
        while (c != null && c != Object.class) {
            champs.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        return champs;
    }

    /**
     * Injecte la valeur via le setter setXxx s'il existe (public, type compatible).
     * Retourne false si aucun setter n'est trouvable.
     */
    private static boolean injecterParSetter(Object objet, Field champ, Object valeur) {
        String nomSetter = "set"
                + Character.toUpperCase(champ.getName().charAt(0))
                + champ.getName().substring(1);
        try {
            Method setter = objet.getClass().getMethod(nomSetter, champ.getType());
            setter.invoke(objet, valeur);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


}
