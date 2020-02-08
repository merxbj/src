/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ad7b39wpa.xls;

import cz.cvut.fel.ad7b39wpa.core.*;
import org.joda.time.Interval;

/**
 *
 * @author mrneo
 */
public class XLSAccountStatementReaderBuilder implements AccountStatementReaderBuilder {

    @Override
    public AccountStatementReader build(Callable owner, Interval accountablePeriod) throws ConfigurationException {
        XLSAccountStatementReader asr = new XLSAccountStatementReader();
        asr.setDesiredPeriod(accountablePeriod);
        return asr;
    }
}