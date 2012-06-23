/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author eTeR
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class Logout extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Destroys the session for this user.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirects back to the initial page.
        response.sendRedirect(request.getContextPath());
    }
}
