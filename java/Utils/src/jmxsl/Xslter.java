/*
 * Xslter
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
package jmxsl;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Xslter {
    public static void main(String[] args) throws Exception {
        String xml = args[0];
        String xsl = args[1];
        String output = args[2];
        
        TransformerFactory factory = TransformerFactory.newInstance();
        File xslFile = new File(xsl);
        Source xslSource = new StreamSource(xslFile);
        Transformer transform = factory.newTransformer(xslSource);
        
        File xmlFile = new File(xml);
        Source xmlInputSource = new StreamSource(xmlFile);
        
        File outputFile = new File(output);
        Result outputResult = new StreamResult(outputFile);
        
        transform.transform(xmlInputSource, outputResult);
        
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(outputFile);
        Node tranXml = doc.getDocumentElement().getElementsByTagName("tran_xml").item(0);
        
        System.out.println(tranXml.toString());
        
        System.out.println("Transformation successfull ...");
    }
}
