package cz.cvut.fel.a7b39wpa.c03jsp.ctrl;

import cz.cvut.fel.a7b39wpa.c03jsp.model.Player;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Jednoduchy kontroler aplikace, ktery provede zpracovani prijateho podzadavku
 * a zvoli spravne view. Pripadne validuje formular.
 *
 * @author Tomas Kadlec
 */
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
        // vychozi akce - seznam
        String viewUrl = "/index.jsp";
        request.setAttribute("showPage", "list");
        // ziskani session, pokud neexistuje, vytvori se nova; v pripade chyby se zobrazi error.jsp (HTML stranka)
        HttpSession session = request.getSession();
        if (session == null) {
            viewUrl = "/error.jsp";
        } else {
            // pokud se session prave vytvorila, musime provest inicializaci
            if (session.isNew()) {
                session.setAttribute("players", new ArrayList<Player>());
            }
            // uloziste zprav je k kontextu pozadavku, mame chybu a potvrzeni
            StringBuffer msgErr = new StringBuffer();
            // ziskani parametru HTTP GET/POST s nazvem akce
            String action = request.getParameter("akce");
            // pokud je akce ulozeni formulare, provedeme validaci a uvidi se
            if (action != null && action.compareTo("pridej") == 0) {
                // indikace, ze formular byl validni
                boolean valid = true;
                // validace jmena
                String name = request.getParameter("jmeno");
                if (name == null || name.trim().compareTo("") == 0) {
                    valid = false;
                    msgErr.append("Jméno musí být zadáno. ");
                }
                // validace skore
                int score = 0;
                try {
                    score = Integer.parseInt(request.getParameter("skore"));
                } catch (NumberFormatException e) {
                    valid = false;
                    msgErr.append("Skóre musí být celé číslo. ");
                }
                // vyhodnoceni vysledku validace
                if (valid) {
                    Player player = new Player();
                    player.setName(name);
                    player.setScore(score);
                    // vlozeni hrace do kolekce
                    ((ArrayList<Player>) session.getAttribute("players")).add(player);
                    request.setAttribute("msgack", new StringBuffer("Hráč byl úspěšně přidán."));
                } else {
                    request.setAttribute("showPage", "form");
                    request.setAttribute("msgerr", msgErr);
                }
            } // pokud je ale akce formular, zobrazime formular
            else if (action != null && action.compareTo("formular") == 0) {
                request.setAttribute("showPage", "form");
            }
        }
        // predani rizeni na zvoleny servlet obstaravajici view
        RequestDispatcher rd = getServletContext().getRequestDispatcher(viewUrl);
        try {
            rd.forward(request, response);
        } // obsluha libovolne vyjimky - pokud to nedopadne, akorat zapiseme na chybovy vystup
        catch (Exception e) {
            System.err.println("Nepodarilo se provest predani rizeni.");
            return;
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
