/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SimpleFinancialJournal;

/**
 *
 * @author eTeR
 */
public class Money {

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%f", value / 100.0);
    }
    
    private int value;
}
