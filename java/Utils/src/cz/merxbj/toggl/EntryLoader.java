/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.util.List;

/**
 *
 * @author jm185267
 */
public interface EntryLoader {
    List<TimeEntry> load();
}
