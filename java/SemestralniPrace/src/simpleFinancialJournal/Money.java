package simpleFinancialJournal;

import java.io.*;

/**
 *
 * @author eTeR
 */
public class Money implements Serializable, Comparable<Money> {

    public Money(long value) {
        this.value = value;
    }

    public Money(double value) {
        this.value = Math.round(value * 100);
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

    public int compareTo(Money m) {
        Long amount1 = value;
        Long amount2 = m.value;
        return amount1.compareTo(amount2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Money other = (Money) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (this.value ^ (this.value >>> 32));
        return hash;
    }

    private long value;
}
