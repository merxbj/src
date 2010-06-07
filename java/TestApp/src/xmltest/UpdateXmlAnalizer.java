package xmltest;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class UpdateXmlAnalizer {

    private static Set<String> nodeSet = new TreeSet<String>();

    public static void main(String[] args) {
        try {
            final String fileIn = "c:/temp/sheetz migration/EP download/tidy_Transformed_LHWEB1000623-2371255.xml";

            DocumentBuilder dom = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dom.parse(new File(fileIn));
            XPath xp = XPathFactory.newInstance().newXPath();

            NodeList nl = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                nodeSet.add(n.getNodeName());
            }

            for (String nodeName : nodeSet) {
                System.out.println(nodeName);
            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
