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
import util.Utils;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> listeControllers = new ArrayList<>();

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

            System.out.println("[Framework] Scan termine. " + listeControllers.size() + " controleur(s) reconnu(s).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode centrale qui gère toutes les requêtes
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // On récupère l'URL complète tapée par l'utilisateur
        String urlDemandee = request.getRequestURL().toString();
        String methodeHttp = request.getMethod();
        
        // On affiche le résultat directement sur l'écran du navigateur
        out.println("<html>");
        out.println("<head><title>Sprint 0</title></head>");
        out.println("<body>");
        out.println("<h1>[Sprint 0] Mon Framework Spring MVC</h1>");
        out.println("<p>Le FrontControllerServlet a bien intercepte la requete.</p>");
        out.println("<strong>L'URL detectee est :</strong> " + urlDemandee);
        out.println("<br><strong>Methode HTTP utilisee :</strong> " + methodeHttp);
       // out.println("<br><strong>Nombre de controleurs detectes :</strong> " + listeControllers.size());
        out.println("<br><strong>Controleurs detectes :</strong>");
        out.println("<ul>");
        for (Class<?> controleur : listeControllers) {
            out.println("<li>" + controleur.getSimpleName() + "</li>");
        }
        out.println("</ul>");
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