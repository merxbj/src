/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.entity.Player;
import services.exception.SignInFailedException;

/**
 *
 * @author eTeR
 */
@Stateless
@LocalBean
public class LoginService {

    @PersistenceContext(unitName = "Kingdoms4Web-ejbPU")
    protected EntityManager em;
    
    public Player signIn(String login, String password) throws SignInFailedException {
        Query query = em.createNamedQuery("Player.findByNick");
        query.setParameter("nick", login);
        List<Player> players = query.getResultList();
        if (players.size() == 1) {
            Player player = players.get(0);
            if (player.getSimlePassword().equals(password)) {
                return player;
            } else {
                throw new SignInFailedException("Invalid password!");
            }
        } else if (players.size() > 1) {
            throw new SignInFailedException("More players with the same login - this should never happen!");
        }
        throw new SignInFailedException("Invalid Login!");
    }
    
}
