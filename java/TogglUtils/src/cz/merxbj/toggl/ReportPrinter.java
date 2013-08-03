/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

/**
 *
 * @author jm185267
 */
public interface ReportPrinter<E> {
    void print(E reportEntries);
}
