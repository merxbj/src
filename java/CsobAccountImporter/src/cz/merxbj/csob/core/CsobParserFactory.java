/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author merxbj
 */
public class CsobParserFactory {

    public static CsobParser createParser(String fileName) {
        if (fileName.endsWith("txt")) {
            return new TextFilesCsobParser();
        } else {
            throw new NotImplementedException();
        }
    }

}
