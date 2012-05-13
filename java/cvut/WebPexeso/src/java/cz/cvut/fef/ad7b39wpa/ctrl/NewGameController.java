/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.ctrl;

import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.core.GameFactory;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author eTeR
 */
public class NewGameController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] types = req.getParameterValues("PlayerType");
        String[] names = req.getParameterValues("PlayerName");
        Game game = GameFactory.createNewGame(types, names);
        req.getSession().setAttribute("game", game);

        String url = resp.encodeRedirectURL("Game");
        resp.sendRedirect(url);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/newGameForm.jsp");
        rd.forward(req, resp);
    }

}
