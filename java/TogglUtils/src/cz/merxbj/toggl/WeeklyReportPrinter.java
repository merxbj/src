/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

/**
 *
 * @author jm185267
 */
public class WeeklyReportPrinter implements ReportPrinter<WeeklyReport> {

    public WeeklyReportPrinter() {
    }

    @Override
    public void print(WeeklyReport report) {
        System.out.println(report);
    }
    
}
