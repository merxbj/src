/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control.remote;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ejb.Remote;
import model.Accountable;
import model.User;

/**
 *
 * @author merxbj
 */
@Remote
public interface AccountableControl {

    Map<String, BigDecimal> getAccountedMoneyPerCalee(User user);

    Map<String, Long> getAccountedUnitsPerCalee(User user);

    List<Accountable> getAllAccountables(User user);

    void importFromFile(InputStream file, User user);

}
