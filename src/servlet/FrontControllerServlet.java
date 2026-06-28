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
import util.Utils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
                        String[] methodesHttp = annotation.method();

                        // Pour chaque methode HTTP declaree, on cree une entree dans le carnet
                        for (String methodHttp : methodesHttp) {
                            String cle = url + " " + methodHttp.toUpperCase();
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

    response.setContentType("text/html;charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();

    // On recupere juste le chemin de l'URL sans le nom de l'application
    // ex: http://localhost:8080/TestApplication/emp/list -> /emp/list
    String contextPath = request.getContextPath();
    String urlComplete = request.getRequestURI();
    String url = urlComplete.substring(contextPath.length());

    out.println("<html>");
    out.println("<head><title>Sprint 2</title></head>");
    out.println("<body>");
    out.println("<h1>[Sprint 3] Mon Framework Spring MVC</h1>");
    out.println("<p>URL demandee : <strong>" + url + "</strong></p>");


    String methodeHttp = request.getMethod().toUpperCase();
    String cle = url + " " + methodeHttp;
    // On cherche cette cle (URL) dans notre carnet de routage
    if (urlMapping.containsKey(cle)) {
        // URL trouvee -> on appelle la methode correspondante
        Method methode = urlMapping.get(cle);
        out.println("<p>Methode trouvee : <strong>" + methode.getName() + "</strong></p>");
        out.println("<p>Dans le controleur : <strong>" + methode.getDeclaringClass().getSimpleName() + "</strong></p>");
    } else {
        // URL inconnue -> on affiche une erreur + la liste des URLs connues
        out.println("<p style='color:red;'><strong>Erreur : URL inconnue !</strong></p>");
        out.println("<p>URLs disponibles dans ce framework :</p>");
        out.println("<ul>");
        for (String urlConnue : urlMapping.keySet()) {
            out.println("<li>" + urlConnue + "</li>");
        }
        out.println("</ul>");
    }

    out.println("</body>");
    out.println("</html>");
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