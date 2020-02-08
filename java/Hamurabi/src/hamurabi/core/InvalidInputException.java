/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author merxbj
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(Throwable thrwbl) {
        super(thrwbl);
    }

    public InvalidInputException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public InvalidInputException(String string) {
        super(string);
    }

    public InvalidInputException() {
    }

}
