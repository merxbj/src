/*
 * WeaponBean
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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import model.entity.Arms;
import model.session.ArmsFacade;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
@Named
@SessionScoped
public class ArmsBean implements Serializable {
    
    @EJB
    protected ArmsFacade armsFacade;

    public ArmsBean() {
    }
    
    @PostConstruct
    protected void init() {
        
    }

    public List<Arms> findAllWeapons() {
        
        List<Arms> weapons = new ArrayList<Arms>();
        List<Arms> arms = armsFacade.findAll();        
        
        for (Arms item : arms) {
            if (item.getType().equals("weapon")) {
                weapons.add(item);
            }
        }

        return weapons;
    }
    
}
