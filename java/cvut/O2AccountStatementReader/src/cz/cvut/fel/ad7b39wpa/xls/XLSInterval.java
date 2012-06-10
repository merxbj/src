/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.Interval;
import java.util.Date;


/**
 *
 * @author eTeR
 */
public class XLSInterval implements Interval {

    private Date startDate;
    private Date endDate;
    
    public XLSInterval() {
        
    }

    public XLSInterval(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setStartDate(Date startDate) {
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