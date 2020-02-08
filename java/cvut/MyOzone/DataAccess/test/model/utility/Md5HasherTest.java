/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.utility;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eTeR
 */
public class Md5HasherTest {

    /**
     * Test of secureString method, of class Md5Hasher.
     */
    @Test
    public void testSecureString() {
        System.out.println("secureString");
        String string = "prsten";
        Md5Hasher instance = new Md5Hasher();
        String expResult = "6b78a17d74ebc628e24767c44324e92f";
        String result = instance.secureString(string);
        assertEquals(expResult, result);
    }

}