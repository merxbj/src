package xmlanalyze;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlTester {
    
    public static void main(String[] args) {
        resolveTransformed();
    }

    private static void resolvePlain() {
        try {
            final String fileIn = "c:/temp/tidy_LHWEB1000623-2371255.xml";
            final String fileOut = "c:/temp/tidy2_LHWEB1000623-2371255.xml";

            DocumentBuilder dom = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dom.parse(new File(fileIn));
            XPath xp = XPathFactory.newInstance().newXPath();

            NodeList nl = (NodeList) xp.evaluate("/LHWEB/Discounts/Discount", doc.getDocumentElement(), XPathConstants.NODESET);
            System.out.println(nl.getLength());

            int fixedCount = 0;
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i).getAttributes().getNamedItem("Name");
                if (n.getTextContent().length() > 30) {
                    n.setTextContent(n.getTextContent().substring(0, 29));
                    fixedCount++;
                }
            }

            System.out.println(String.format("Fixed %d descriptions", fixedCount));

            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(new File(fileOut)));

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private static void resolveTransformed() {
        try {
            final String fileIn = "c:/temp/Transformed_LHWEB1000474-2371294.xml";
            final String fileOut = "c:/temp/FIXED_Transformed_LHWEB1000474-2371294.xml";

            DocumentBuilder dom = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dom.parse(new File(fileIn));
            XPath xp = XPathFactory.newInstance().newXPath();

            NodeList nl = (NodeList) xp.evaluate("/LHUpdate/Discount/description", doc.getDocumentElement(), XPathConstants.NODESET);
            System.out.println(nl.getLength());

            int fixedCount = 0;
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getTextContent().length() > 30) {
                    n.setTextContent(n.getTextContent().substring(0, 29));
                    fixedCount++;
                }
            }

            System.out.println(String.format("Fixed %d descriptions", fixedCount));

            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(new File(fileOut)));

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
