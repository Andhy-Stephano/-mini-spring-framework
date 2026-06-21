package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

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