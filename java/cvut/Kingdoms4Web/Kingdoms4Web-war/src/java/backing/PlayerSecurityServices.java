/*
 * LoginBean
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package backing;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import model.entity.Player;
import services.LoginService;
import services.exception.SignInFailedException;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
@Named
@SessionScoped
public class PlayerSecurityServices implements Serializable {
    
    @EJB
    LoginService service;
    
    protected String login;
    protected String password;

    protected Player currentPlayer;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String signIn() {
        try {
            currentPlayer = service.signIn(login, password);
            return "success";
        } catch (SignInFailedException ex) {
            return "failed";
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public String getIndexPageContent() {
        if (currentPlayer != null) {
            return "playersummary.xhtml";
        }
        return "login.xhtml";
    }

}
