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
import util.Utils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import util.JsonSerializer;
import annotation.RestAPI;

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

                // On appelle la methode et on recupere le resultat
                Object resultat = methode.invoke(controleurInstance);

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
            out.println("<html><body>");
            out.println("<h1>[Sprint 6] Mon Framework Spring MVC</h1>");
            out.println("<p>URL : <strong>" + url + "</strong></p>");
            out.println("<p>Methode : <strong>" + methode.getName() + "</strong></p>");
            out.println("<p>Controleur : <strong>" +
                methode.getDeclaringClass().getSimpleName() + "</strong></p>");
            out.println("</body></html>");
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
