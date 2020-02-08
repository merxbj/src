/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.control.remote;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ejb.Remote;
import model.Accountable;
import model.User;

/**
 * Remote interface to simplify the access to the accountable entities.
 *
 * @author merxbj
 */
@Remote
public interface AccountableControl {

    /**
     * Gets aggregated accountable data (money accounted) per calee.
     * @param user who is the actual caller
     * @return Map mapping total accounted money to a called number.
     */
    Map<String, BigDecimal> getAccountedMoneyPerCalee(User user);

    /**
     * Gets aggregated accountable data (units accounted) per calee.
     * @param user who is the actual caller
     * @return Map mapping total accounted units to a called number.
     */
    Map<String, Long> getAccountedUnitsPerCalee(User user);

    /**
     * Gets all accountables associated with the given user.
     * @param user who is the actual owner (caller)
     * @return list of all accountables associated with the given user
     */
    List<Accountable> getAllAccountables(User user);

    /**
     * Given the byte content of an import XLS file (with a predefined format)
     * parses loads and parses the file and imports all the accountables to the
     * database and associates them with the given user.
     * @param contents the actual byte content of the import file
     * @param user the actual owner (caller) of the accountable data
     */
    void importFromFile(byte[] contents, User user);

}
