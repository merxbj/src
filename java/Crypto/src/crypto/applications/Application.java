/*
 * Application
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
package crypto.applications;

import crypto.core.AffineCipher;
import crypto.core.AffineCipherKey;
import crypto.core.Cipher;
import crypto.core.Key;
import crypto.core.KeyCipher;
import crypto.core.KeyCipherKey;
import crypto.core.PlainShiftKey;
import crypto.core.TableCipher;
import crypto.utils.CryptoVocabulary;
import crypto.utils.SilentVocabularyAnalyzeSink;
import crypto.utils.VerbooseVocabularyAnalyzeSink;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Application {
    public static void main(String[] args) {
        
        //excerciseOne(args);
        //excerciseTwo(args);
        //excerciseThree(args);
        excerciseFour(args);
        
    }
    
    public static void excerciseOne(String[] args) {
        String ciphered = "QYFFNBYPYLSHYRNXUSBYLBOMVUHXFYXBYLNINBYMNLUHAYLIIGUAUCHUHXNBYLYQUMNBYXUSMZIIXUHXUMJCHHCHAQBYYFUHXUALYUNVOHXFYIZZFUR";
        char[] cipheredChars = ciphered.toCharArray();
        
        Cipher cipher = new AffineCipher();
        for (byte shift = 1; shift < 26; shift++) {
            Key key = new PlainShiftKey(shift);
            cipher.assignKey(key);
            char[] deciphered = cipher.decipher(cipheredChars);
            System.out.println(new String(deciphered) + " | " + key);
        }
    }
    
    public static void excerciseTwo(String[] args) {
        String ciphered = "NDUHUCYQIHUSCRUIHKICKDUIKBYPGNUIKBYPGNUIVRHUSUSLUHGXNDUHUIHUVYNXGTUWDYPUKMUGVKNYVGADNGXUIHCYQHDUIRWGPPOYSUYXX";
        char[] cipheredChars = ciphered.toCharArray();
        
        CryptoVocabulary voc = new CryptoVocabulary(new VerbooseVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        
        Cipher cipher = new AffineCipher();

        for (byte b = 0; b <= 25; b++) {
            byte[] a = {1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25};
            for (byte i = 0; i < a.length; i++) {
                Key key = new AffineCipherKey(a[i], b);
                cipher.assignKey(key);
                char[] openChars = cipher.decipher(cipheredChars);
                String deciphered = new String(openChars);
                if (voc.isSentenseNoSpaces(deciphered, 4, 4)) {
                    System.out.println(deciphered + " : " + key);
                }
            }
        }
    }
    
    private static int wordsCount = 0;
    private static int wordsProcessed = 0;
    private static List<String> sentenses = new ArrayList<String>();
    
    public static void excerciseThree(String[] args) {
        
        String ciphered = "CSSNCSQNTRTOCJSKSPTHRGTCJACISTPNTNCAOKJTCWCYCJAGKEFTASNTAKKPQNTWCQDUQSSNBJFBJOKICOKKAEPYWNTJQNTNTCPACMUTTPFJKEFBJOCSSNTWBJAKWQNTULLTACSKJETCJAKLTJTABSCJAQUPTTJKUONSNTPTWCQSNTQHCGGGBSSGTRGCEFSNBJOQBSSBJOKJSNTWBJAKWGTAOTACJOGBJOBSQRTCUSBIUGSKTQCJASWBPGBJOBSQSCBGQKSNCSYKUEKUGAQECPETGYQTTBS";
        final char[] cipheredChars = ciphered.toCharArray();
        
        final CryptoVocabulary voc = new CryptoVocabulary(new SilentVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        wordsCount = voc.size();

        final Object lock = new Object();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        for (final String word : voc) {
            Runnable task = new Runnable() {

                @Override
                public void run() {
                    boolean foundSentense = false;
                    Cipher cipher = new KeyCipher();
                    Key key = new KeyCipherKey(word);
                    cipher.assignKey(key);
                    char[] openChars = cipher.decipher(cipheredChars);
                    String deciphered = new String(openChars);
                    foundSentense = voc.isSentenseNoSpaces(deciphered, 5, 15);
                    
                    synchronized (lock) {
                        System.out.println(String.format("\rAnalyzed %5.2f%% | %d words remaining", 100.*wordsProcessed++ / (wordsCount), wordsCount - wordsProcessed));
                        if (foundSentense) {
                            sentenses.add(deciphered + " : " + key);
                        }
                    }
                }
            };
            
            threadPool.execute(task);
        }
        
        try {
            System.out.println("All tasks queued up - awaiting termination.");
            threadPool.shutdown();
            threadPool.awaitTermination(15, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        
        System.out.println("Let's take a look what have we found ...");
        for (String sentense : sentenses) {
            System.out.println(sentense);
        }

    }

    private static void excerciseFour(String[] args) {
        String ciphered = "GAEXOTHYHSGTNGNOUHSDHUOEYOHDIGOTAHGATUROOEHVSDYNAITTMVUDKAEHMSDRRTHAIKAIDHEOAOPLHEYCNPNPELRYVTSEWBTOPGRRINSEHOFIEUWEHOEKITRESLNSASTEMVENHTRHADULHIRIEDGAHEEXORSETHSRTMTESGAWEFHEUEYOYCFAANAAOAASDTEBOLGVDNTRDNBTHAEMAOESDEAADAOIT";
        char[] cipheredChars = ciphered.toCharArray();
        
        CryptoVocabulary voc = new CryptoVocabulary(new VerbooseVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        
        //Cipher cipher = new TableCipher();
    }
}
