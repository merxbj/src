/*
 * Trifid
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Testing vectors:
 * 
 * e radio**QQQ**5 Education is what remains after one has forgotten everything he learned in school.
 * end
 *
 * DD#EO QFLMJ YBF#W ARXHI MPROH RH#XL HFRNG IXIAP ITYYW EFQGX K#DKZ RHDY# AQMXK FGLL
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Trifid {

    public static void main(String[] args) {
        
        String input = "e radio**QQQ**5 Education is what remains after one has forgotten everything he learned in school.\nend";
        byte[] inputBytes = input.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(inputBytes);
        
        List<SecureMessage> messages = loadInputFromStream(stream);
        List<SecureMessage> invertedMessages = invertMessages(messages);

        for (SecureMessage invertedMessage : invertedMessages) {
            System.out.println(invertedMessage);
        }
    }
    
    private static List<SecureMessage> loadInputFromStream(InputStream stream) {
        List<SecureMessage> messages = new ArrayList<SecureMessage>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        SecureMessageFactory factory = new SecureMessageFactory();
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.startsWith("end")) {
                    SecureMessage message = factory.createMessage(line);
                    messages.add(message);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return messages;
    }

    private static List<SecureMessage> invertMessages(List<SecureMessage> messages) {
        List<SecureMessage> invertedMessages = new ArrayList<SecureMessage>(messages.size());
        TrifidCipher cipher = new TrifidCipher();
        for (SecureMessage message : messages) {
            SecureMessage invertedMessage = message.invert(cipher);
            invertedMessages.add(invertedMessage);
        }
        return invertedMessages;
    }

}

/*
 * try {
            boolean endEncountered = false;
            while ((stream.available() > 0) && !endEncountered) {
                char character = (char) stream.read();
                switch (character) {
                    case 'd':
                        SecureMessage cipheredMessage = new CipheredMessage();
                        if (cipheredMessage.parse(stream)) {
                            messages.add(cipheredMessage);
                        }
                        break;
                    case 'e':
                        if (stream.available() > 2) {
                            SecureMessage openedMessage = new OpenedMessage();
                            if (openedMessage.parse(stream)) {
                                messages.add(openedMessage);
                            }
                        } else {
                            // assume that when after 'e' there are only 2 or less characters left
                            // in the stream, it is the 'end' at the end of the input
                            endEncountered = true;
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
 */