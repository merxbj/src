package cz.cvut.fel.ad7b39wpa.core;

/**
 * Builds an AccountStatementReader based on the given parameters.
 * The account statement format depends on the Callable owner (recipient of the
 * account statement) and the Interval that is being accounted
 * @author jmerxbauer
 */
public interface AccountStatementReaderBuilder {
    
    /**
     * Builds an AccountStatementReader based on the given parameters.
     * The account statement format depends on the Callable owner (recipient of the
     * account statement) and the Interval that is being accounted
     * @param owner the recipient of the account statement that is going to be read.
     * @param accuntablePeriod the accounted interval of the statement that is going to be read.
     * @return the account statement reader.
     * @throws ConfigurationException if the supplied parameters don't make sense
     */
    public AccountStatementReader build(Callable owner, Interval accuntablePeriod) throws ConfigurationException;
}
