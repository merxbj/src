/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleFinancialJournal;

import java.io.*;

/**
 *
 * @author eTeR
 */
public class Money implements Serializable {

    public Money(long value) {
        this.value = value;
    }
    
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%6.2f", value / 100.0);
    }
    
    private long value;
}
