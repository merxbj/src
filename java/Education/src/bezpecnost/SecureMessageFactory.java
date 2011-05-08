/*
 * SecureMessageFacotyr
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package bezpecnost;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class SecureMessageFactory {
    public SecureMessage createMessage(String line) {
        String upperCase = line.toUpperCase();
        switch (upperCase.charAt(0)) {
            case 'E' :
                return parseEncryptInstruction(upperCase);
            case 'D' :
                return parseDecryptInstruction(upperCase);
            default :
                throw new RuntimeException("Unexpected token!");
        }
    }

    private SecureMessage parseEncryptInstruction(String line) {
        final int beginOfKey = 2;
        final int endOfKey = line.indexOf(" ", beginOfKey);

        TrifidKey key = parseKey(line.substring(beginOfKey, endOfKey));
        String text = line.substring(endOfKey + 1);
        
        return new OpenedMessage(text.toCharArray(), key);
    }

    private SecureMessage parseDecryptInstruction(String line) {
        final int beginOfKey = 2;
        final int endOfKey = line.indexOf(" ", beginOfKey);

        TrifidKey key = parseKey(line.substring(beginOfKey, endOfKey));
        String text = line.substring(endOfKey + 1);
        
        return new CipheredMessage(text.toCharArray(), key);
    }
    
    private TrifidKey parseKey(String keySubString) {
        String[] keyAndClustering = keySubString.split("\\*\\*QQQ\\*\\*");

        String key = keyAndClustering[0];
        String clusteringString = keyAndClustering[1];
        
        int clustering = 0;
        try {
            clustering = Integer.parseInt(clusteringString); 
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return new TrifidKey(key.toCharArray(), clustering);
    }
}
