/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Player;

/**
 *
 * @author eter
 */
public class ListView extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        try {
            
            HttpSession session = request.getSession();
            if (session == null) {
                return; // maybe something more smart
            }
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Player Chart - Player List</title>");  
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/Header");
            if (dispatcher != null) {
                dispatcher.include(request, response);
            } else {
                throw new RuntimeException("Failed to include /Header");
            }

            printPlayerList(out, session);

            out.println("</body>");
            out.println("</html>");
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
    
    private void printPlayerList(PrintWriter out, HttpSession session) {
        List<Player> players = (List<Player>) session.getAttribute("PlayerList");
        if (players != null) {
            out.println("<ul>");
            for (Player p : players) {
                out.println("<li>");
                out.printf("%s | %d", p.getName(), p.getScore());
                out.println("</li>");
            }
            out.println("</ul>");
        }
    }
}
