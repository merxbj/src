/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ad7b39wpa.mock;

import cz.cvut.fel.ad7b39wpa.core.Interval;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author eTeR
 */
public class IntervalMock implements Interval {

    private static Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private static long ONE_DAY_AS_MILLISECONDS = 24L * 60L * 60L * 1000L;

    private Date startDate;
    private Date endDate;

    public static Interval createRandomInterval(int maxDaysBack, int daysDuration) {
        if (maxDaysBack < daysDuration) {
            throw new RuntimeException(String.format("maxDaysBack(%d) < daysDuration(%d)", maxDaysBack, daysDuration));
        }

        int daysBack = random.nextInt(maxDaysBack);
        long startMilis = Calendar.getInstance().getTimeInMillis() - (daysBack * ONE_DAY_AS_MILLISECONDS);
        
        Interval interval = new IntervalMock();
        interval.setStartDate(new Date(startMilis));
        interval.setEndDate(new Date(startMilis + (daysDuration * ONE_DAY_AS_MILLISECONDS)));

        return interval;
    }

    private IntervalMock() {
        
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

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
        final IntervalMock other = (IntervalMock) obj;
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