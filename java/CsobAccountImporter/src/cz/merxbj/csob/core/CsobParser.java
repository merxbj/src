/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import java.io.InputStream;
import java.util.Collection;

/**
 *
 * @author merxbj
 */
public interface CsobParser {
    public Collection<Transaction> parse(InputStream is);
}
