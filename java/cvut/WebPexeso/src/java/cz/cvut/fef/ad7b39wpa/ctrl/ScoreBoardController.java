/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.ctrl;

import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.score.ScoreBoard;
import java.io.IOException;
import javax.jms.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author eTeR
 */
public class ScoreBoardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Object o1 = req.getSession().getAttribute("scoreBoard");
        if (o1 == null) {
            o1 = new ScoreBoard();
        }
        ScoreBoard board = (ScoreBoard) o1;

        Object o = req.getAttribute("completedGame");
        if (o != null) {
            board.analyzeGame((Game) o);
        }

        req.getSession().setAttribute("scoreBoard", board);

        RequestDispatcher rd = getServletContext().getRequestDispatcher("/scoreBoard.jsp");
        rd.forward(req, resp);
    }

}
