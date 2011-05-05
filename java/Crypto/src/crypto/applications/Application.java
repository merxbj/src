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

import crypto.core.*;
import crypto.utils.*;
import java.io.PrintStream;
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
        
        long start = System.nanoTime();
        //excerciseOne(args);
        //excerciseTwo(args);
        //excerciseThree(args);
        //excerciseFour(args);
        //excerciseFive(args);
        excerciseSix(args);
        System.out.printf("The task took %d miliseconds", (System.nanoTime() - start) / 1000000 );
    }

    public static void excerciseOne(String[] args) {
        String ciphered = "QYFFNBYPYLSHYRNXUSBYLBOMVUHXFYXBYLNINBYMNLUHAYLIIGUAUCHUHXNBYLYQUMNBYXUSMZIIXUHXUMJCHHCHAQBYYFUHXUALYUNVOHXFYIZZFUR";
        char[] cipheredChars = ciphered.toCharArray();
        
        Cipher cipher = new AffineCipher();
        for (char shift = 1; shift < 26; shift++) {
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

        for (char b = 0; b <= 25; b++) {
            char[] a = {1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25};
            for (int i = 0; i < a.length; i++) {
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
    

    static int wordsCount = 0; // for logging purposes
    static int wordsProcessed = 0; // for logging purposes
    static List<String> sentenses = new ArrayList<String>();
    
    public static void excerciseThree(String[] args) {
        
        String ciphered = "CSSNCSQNTRTOCJSKSPTHRGTCJACISTPNTNCAOKJTCWCYCJAGKEFTASNTAKKPQNTWCQDUQSSNBJFBJOKICOKKAEPYWNTJQNTNTCPACMUTTPFJKEFBJOCSSNTWBJAKWQNTULLTACSKJETCJAKLTJTABSCJAQUPTTJKUONSNTPTWCQSNTQHCGGGBSSGTRGCEFSNBJOQBSSBJOKJSNTWBJAKWGTAOTACJOGBJOBSQRTCUSBIUGSKTQCJASWBPGBJOBSQSCBGQKSNCSYKUEKUGAQECPETGYQTTBS";
        final char[] cipheredChars = ciphered.toCharArray();
        
        final CryptoVocabulary voc = new CryptoVocabulary(new SilentVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        wordsCount = voc.size();

        final Object lock = new Object();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        sentenses.clear();

        for (final String word : voc) {
            Runnable task = new Runnable() {

                @Override
                public void run() {
                    Cipher cipher = new KeyCipher();
                    Key key = new KeyCipherKey(word);
                    cipher.assignKey(key);
                    char[] openChars = cipher.decipher(cipheredChars);
                    String deciphered = new String(openChars);
                    boolean foundSentense = voc.isSentenseNoSpaces(deciphered, 5, 15);
                    
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
        
        PrintStream stream = null;
        try {
            if (args.length >= 2) {
                stream = new PrintStream(args[1]);
            } else {
                stream = System.out;
            }

            stream.println("Let's take a look what have we found ...");
            for (String sentense : sentenses) {
                stream.println(sentense);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            stream.close();
        }

    }

    
    private static int keysProcessed = 0;

    private static void excerciseFour(String[] args) {
        final String ciphered = "GAEXOTHYHSGTNGNOUHSDHUOEYOHDIGOTAHGATUROOEHVSDYNAITTMVUDKAEHMSDRRTHAIKAIDHEOAOPLHEYCNPNPELRYVTSEWBTOPGRRINSEHOFIEUWEHOEKITRESLNSASTEMVENHTRHADULHIRIEDGAHEEXORSETHSRTMTESGAWEFHEUEYOYCFAANAAOAASDTEBOLGVDNTRDNBTHAEMAOESDEAADAOIT";
        final char[] cipheredChars = ciphered.toCharArray();

        final CryptoVocabulary voc = new CryptoVocabulary(new SilentVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        
        final TableCipherKeyGenerator keyGenerator = new TableCipherKeyGenerator(cipheredChars.length);
        
        final Object lock = new Object();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        sentenses.clear();
        
        for (final Key key : keyGenerator) {
            Runnable task = new Runnable() {

                @Override
                public void run() {
                    Cipher cipher = new CompleteTableCipher();
                    cipher.assignKey(key);
                    char[] openChars = cipher.decipher(cipheredChars);
                    String deciphered = new String(openChars);
                    boolean foundSentense = voc.isSentenseNoSpaces(deciphered, 4, 10);

                    synchronized (lock) {
                        System.out.println(String.format("\rAnalyzed %5.2f%% | %d sizes remaining", 100.*keysProcessed++ / (keyGenerator.count()), keyGenerator.count() - keysProcessed));
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
        
        PrintStream stream = null;
        try {
            if (args.length >= 2) {
                stream = new PrintStream(args[1]);
            } else {
                stream = System.out;
            }

            stream.println("Let's take a look what have we found ...");
            for (String sentense : sentenses) {
                stream.println(sentense);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            stream.close();
        }
    }
    
    private static void excerciseFive(String[] args) {
        final String ciphered = "SNDONCHNHHNTHCCESVEVESWWARDAANOSTETKGOPEITEUNEGXANTLOCDTPISXNPHFANQIRNMXIIBSSNAOLNRXUTHEETAEIRTXDENTAGDLUENXSTHHAGHHANEXLIRENNLPHIKXISWHATTSEHWXEAMIAASONREXODTWDKITEEEXEAKDMETEEHAX";
        final char[] cipheredChars = ciphered.toCharArray();

        final CryptoVocabulary voc = new CryptoVocabulary(new SilentVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        
        final DoubleTableCipherKeyGenerator keyGenerator = new DoubleTableCipherKeyGenerator(cipheredChars.length);
        
        final Object lock = new Object();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        sentenses.clear();
        
        for (final DoubleTableCipherKey key : keyGenerator) {
            Runnable task = new Runnable() {

                @Override
                public void run() {
                    Cipher cipher = new DoubleCompleteTableCipher();
                    cipher.assignKey(key);
                    char[] openChars = cipher.decipher(cipheredChars);
                    String deciphered = new String(openChars);
                    boolean foundSentense = voc.isSentenseNoSpaces(deciphered, 4, 10);

                    synchronized (lock) {
                        System.out.println(String.format("\rAnalyzed %5.2f%% | %d sizes remaining", 100.*keysProcessed++ / (keyGenerator.count()), keyGenerator.count() - keysProcessed));
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
        
        PrintStream stream = null;
        try {
            if (args.length >= 2) {
                stream = new PrintStream(args[1]);
            } else {
                stream = System.out;
            }

            stream.println("Let's take a look what have we found ...");
            for (String sentense : sentenses) {
                stream.println(sentense);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            stream.close();
        }
    }
    
    private static long combinationsProcessed = 0;
    
    private static void excerciseSix(String[] args) {
        final String ciphered = "MMQNKIFICINDCTISNJDMMWBNMMSNGMMSMIIGJUNMDJTCMGTDMYDDOITIVJIDNSJMGYSTTMSDMNSJSMYKJTSTSTSTOJCTKTJGDICSCTWTUUINCWCSDTTNCMMDNRGSQNHMWFMWJNTDQQFTHTJYTNNDDTJMGGTKKDCTASQTJQCKJONCUNQNCNJNCTGMDIMIMTSFOMMMIMSNTTTSDJDMNDTUHPTMIDMWTTNTJIVSTNUDYDSDDNSVSIMNMYMDSSTJJQQTNAAJSDWJTATSNTGX";
        final char[] cipheredChars = ciphered.toCharArray();

        final CryptoVocabulary voc = new CryptoVocabulary(new SilentVocabularyAnalyzeSink());
        voc.loadFromFile(args[0]);
        wordsCount = voc.size();
        
        final TableCipherKeyGenerator keyGenerator = new TableCipherKeyGenerator(cipheredChars.length);
        
        final Object lock = new Object();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final long combinationsCount = keyGenerator.count() * voc.size();
        sentenses.clear();
        
        final ResultReporter reporter = new ResultReporter(args);
        
        for (final String word : voc) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    Cipher outerCipher = new KeyCipher();
                    Key outerKey = new KeyCipherKey(word);
                    outerCipher.assignKey(outerKey);
                    char[] outerChars = outerCipher.decipher(cipheredChars);

                    for (final TableCipherKey innerKey : keyGenerator) {
                        Cipher innerCipher = new CompleteTableCipher();
                        innerCipher.assignKey(innerKey);
                        char[] openChars = innerCipher.decipher(outerChars);
                        String deciphered = new String(openChars);
                        boolean foundSentense = voc.isSentenseNoSpaces(deciphered, 5, 12);

                        synchronized (lock) {
                            if (foundSentense) {
                                String found = deciphered + " : " + outerKey + " : " + innerKey;
                                sentenses.add(found);
                                reporter.reportIntermediate(found);
                            }
                            System.out.println(String.format("\rAnalyzed %5.2f%% | %d combinations remaining | %s potentials sentenses found", 100.*combinationsProcessed++ / (combinationsCount), combinationsCount - combinationsProcessed, sentenses.size()));
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
        
        reporter.reportSummary(sentenses);
        reporter.close();
        
    }
    
    private static class ResultReporter {
        private PrintStream stream;

        public ResultReporter(String[] args) {
            try {
                if (args.length >= 2) {
                    stream = new PrintStream(args[1]);
                } else {
                    stream = System.out;
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        
        public void reportSummary(List<String> sentenses) {
            stream.println("Let's take a look what have we found ...");
            for (String sentense : sentenses) {
                stream.println(sentense);
            }
        }
        
        public void reportIntermediate(String sentense) {
            stream.println(sentense);
        }
        
        public void close() {
            this.stream.close();
        }
        
    }
}
