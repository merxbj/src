/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Player;

/**
 *
 * @author eter
 */
@WebServlet(name = "Controller", urlPatterns = {"/start"})
public class Controller extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        if (session != null) {
            initSessionIfNew(session);
        }
        
        String nextView = handleRequest(request, session);

        PrintWriter out = response.getWriter();
        try {
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void initSessionIfNew(HttpSession session) {
        if (session.isNew()) {
            session.setAttribute("PlayerList", new ArrayList<Player>());
        }
    }

    private String handleRequest(HttpServletRequest request, HttpSession session) {
        if (session == null) {
            return "/error.jsp";
        }
        
        String action = request.getParameter("action");
        if ((action == null) || action.equals("")) {
            action = "seznam";
        }
        
        String nextView = "";
        if (action.equals("seznam")) {
            nextView = "/PlayerList";
        } else if (action.equals("formular")) {
            nextView = "/AddPlayer";
        } else if (action.equals("pridej")) {
            try {
                Player player = createPlayer(request);
                addPlayerToSession(player, session);
                nextView = "/PlayerList";
            } catch (PlayerValidationException ex) {
                nextView = "/AddPlayer";
                request.setAttribute("ValidationResult", ex);
            }
        }
        
    }

    private Player createPlayer(HttpServletRequest request) throws PlayerValidationException {
        String name = request.getParameter("jmeno");
        if ((name == null) || name.equals("")) {
            throw new PlayerValidationException("Jmeno neni vyplneno!");
        }
        
        int score = 0;
        try {
            score = Integer.parseInt(request.getParameter("skore"));
        } catch (Exception ex) {
            
        }
    }
}
