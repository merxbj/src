/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.ctrl;

import cz.cvut.fef.ad7b39wpa.model.game.core.Field;
import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.core.GameFactory;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author eTeR
 */
public class GameController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Game serverGame = null;

        HttpSession session = req.getSession();
        Object o = session.getAttribute("game");
        if (o == null) {
            if (req.getParameter("fieldState") != null) {
                // interesting stuff could be here ... we have a empty session but the
                // client is claiming it has a game ... create a new game anyway
                // right now but this is subject of a further change or improvement ...
            }
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/NewGame");
            rd.forward(req, resp);
        } else {
            serverGame = (Game) o;
            if (!serverGame.getGameCompleted()) {
                String fieldState = (String) req.getParameter("fieldState");
                if (fieldState != null) {
                    Field clientField = Field.deserialize(fieldState);
                    serverGame.update(clientField);
                } else {
                    // suspicious - we have a game in the session but the client
                    // didn't give us any serialized game state - ignore for now
                    // but we should figure out how to react properly later on
                }
            }
        }

        if (serverGame != null) {
            if (serverGame.getGameCompleted()) {
                session.setAttribute("game", null);
                req.setAttribute("completedGame", serverGame);
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/ScoreBoard");
                rd.forward(req, resp);
            } else {
                serverGame.prepareForClient();
                req.setAttribute("view", "game");
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/game.jsp");
                rd.forward(req, resp);
            }
        }
    }

}
