/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class to provide a simpler interface to MD5 hashing
 * @author eTeR
 */
public class Md5Hasher {

    private MessageDigest md5;

    /**
     * Creates a new hasher that utilizes the generic interface to digest algorithms
     * to obtain the MD5 algorithm.
     */
    public Md5Hasher() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
    }

    /**
     * Takes the open string and calculates the MD5 hash that is convereted
     * to hexadecimal string and returned back.
     * @param string the string to be secured
     * @return the hashed (secured) string by MD5
     */
    public String secureString(String string) {
        
        byte[] hashed = md5.digest(string.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hashed.length; i++) {
            String hex = Integer.toHexString(0xFF & hashed[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

}
