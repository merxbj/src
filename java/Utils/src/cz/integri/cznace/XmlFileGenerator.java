/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.integri.cznace;

import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mexbik
 */
public class XmlFileGenerator {
    
    private String xmlFilePath;

    XmlFileGenerator(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public void generate(List<Entry> entries) {
        try {
            Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = (Element)xml.appendChild(xml.createElement("Ciselnik"));
            generate(entries, root, xml);
            
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(new DOMSource(xml), new StreamResult(new File(xmlFilePath)));
            
        } catch (ParserConfigurationException | DOMException | TransformerFactoryConfigurationError | TransformerException ex) {
            throw new RuntimeException("Failed to generate the output file " + xmlFilePath, ex);
        }
                    
    }

    private void generate(List<Entry> entries, Element root, Document xml) {
        for (Entry entry : entries) {
            Element row = (Element)root.appendChild(xml.createElement("CZ-NACE"));
            generate(entry, row, xml);
        }
    }
    
    private void generate(Entry entry, Element row, Document xml) {
        Element kod = xml.createElement("Kod_CZNACE");
        kod.setTextContent(entry.getCode());
        
        Element nazev = xml.createElement("Nazev");
        nazev.setTextContent(entry.getName());
        
        row.appendChild(kod);
        row.appendChild(nazev);
    }
    
}
