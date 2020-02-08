/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author eter
 */
public class FormView extends HttpServlet {

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
            out.println("<title>Player Chart - Add New Player</title>");  
            out.println("</head>");
            out.println("<body>");
            
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/Header");
            if (dispatcher != null) {
                dispatcher.include(request, response);
            } else {
                throw new RuntimeException("Failed to include /Header");
            }

            out.println("<h1>Přidat uživatele</h1>");
            
            out.println("<form action=\"" + response.encodeRedirectURL(request.getContextPath())+ "/start\" method=\"POST\">");
            out.println("<div><label>Jméno</label><input type=\"text\" name=\"jmeno\" value=\"" +
                    (request.getParameter("jmeno") != null ? request.getParameter("jmeno") : "" ) + "\" /></div>");
            out.println("<div><label>Skóre</label><input type=\"text\" name=\"skore\" value=\"" +
                    (request.getParameter("skore") != null ? request.getParameter("skore") : "" ) + "\" /></div>");
            out.println("<div><input type=\"hidden\" name=\"action\" value=\"pridej\" /></div>");
            out.println("<div><input type=\"submit\" name=\"odeslat\" value=\"Odeslat\" /></div>");
            out.println("</form>");

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
}
