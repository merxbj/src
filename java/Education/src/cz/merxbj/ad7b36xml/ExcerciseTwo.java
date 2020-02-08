/*
 * ExcerciseTwo
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
package cz.merxbj.ad7b36xml;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class ExcerciseTwo {
    public static void main(String[] args) throws Exception {
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File("C:\\Users\\eTeR\\Documents\\cvut\\ad7b36xml\\u6\\ad7b36.xml"), new DefaultHandler() {

            int maxDepth;
            int currentDepth;
            
            @Override
            public void startDocument() throws SAXException {
                
            }
            
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (++currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                currentDepth--;
            }
            
            @Override
            public void endDocument() throws SAXException {
                System.out.printf("Document maximal depth is: %d\n", maxDepth);
            }

        });
    }
}
