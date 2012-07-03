/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.Interval;
import org.joda.time.DateTime;


/**
 *
 * @author eTeR
 */
public class XLSInterval implements Interval {

    private DateTime startDate;
    private DateTime endDate;
    
    public XLSInterval() {
        
    }

    public XLSInterval(DateTime startDate, DateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public DateTime getEndDate() {
        return endDate;
    }

    @Override
    public DateTime getStartDate() {
        return startDate;
    }

    @Override
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XLSInterval other = (XLSInterval) obj;
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 97 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return startDate + " - " + endDate;
    }

    @Override
    public int compareTo(Interval o) {
        if (o == null) {
            return 1;
        } else if ((startDate == null) || (endDate == null)) {
            return -1;
        }

        int compare = startDate.compareTo(o.getStartDate());
        if (compare == 0) {
            compare = endDate.compareTo(o.getEndDate());
        }
        return compare;
    }

}