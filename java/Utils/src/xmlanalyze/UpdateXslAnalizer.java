package xmlanalyze;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;

public class UpdateXslAnalizer {
    private static Set<String> baseMatchTemplates = new TreeSet<String>();
    private static Set<String> overrideNamedTemplates = new TreeSet<String>();
    private static Set<String> overrideMatchTemplates = new TreeSet<String>();

    private static Set<String> desiredMatchTemplates = new TreeSet<String>();
    private static Set<String> desiredNamedTemplates = new TreeSet<String>();

    public static void main(String[] args) {
        try {
            final String baseFile = "c:/temp/sheetz migration/EP download/transform/Cached_Transform_LHWebPrimary.XSL";
            final String overrideFile = "c:/temp/sheetz migration/EP download/transform/Override_Transform_LHWebPrimary.XSL";

            DocumentBuilder dom = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document baseXsl = dom.parse(new File(baseFile));
            Document overrideXsl = dom.parse(new File(overrideFile));
            XPath xp = XPathFactory.newInstance().newXPath();

            // get all match templates present in base
            NodeList baseTemplatesNodes = (NodeList) xp.evaluate("/stylesheet/template[@match]", baseXsl, XPathConstants.NODESET);
            for (int i = 0; i < baseTemplatesNodes.getLength(); i++) {
                baseMatchTemplates.add(baseTemplatesNodes.item(i).getAttributes().getNamedItem("match").getTextContent());
            }

            // get all match templates present in override
            NodeList overrideTemplatesNodes = (NodeList) xp.evaluate("/stylesheet/template[@match]", overrideXsl, XPathConstants.NODESET);
            for (int i = 0; i < overrideTemplatesNodes.getLength(); i++) {
                overrideMatchTemplates.add(overrideTemplatesNodes.item(i).getAttributes().getNamedItem("match").getTextContent());
            }

            // get all named templates present in override
            NodeList overrideNamedTemplatesNodes = (NodeList) xp.evaluate("/stylesheet/template[@name]", overrideXsl, XPathConstants.NODESET);
            for (int i = 0; i < overrideNamedTemplatesNodes.getLength(); i++) {
                overrideNamedTemplates.add(overrideNamedTemplatesNodes.item(i).getAttributes().getNamedItem("name").getTextContent());
            }

            // analyze match templates
            for (String template : baseMatchTemplates) {
                if (!overrideMatchTemplates.contains(template)) {
                    desiredMatchTemplates.add(template);
                }
            }

            // analyze named templates
            for (String template : desiredMatchTemplates) {
                NodeList childNamedTemplateCalls = (NodeList) xp.evaluate("/stylesheet/template[@match='"+template+"']//call-template", baseXsl, XPathConstants.NODESET);
                for (int i = 0; i < childNamedTemplateCalls.getLength(); i++) {
                    String templateToCall = childNamedTemplateCalls.item(i).getAttributes().getNamedItem("name").getTextContent();
                    if (!overrideNamedTemplates.contains(templateToCall)) {
                        desiredNamedTemplates.add(templateToCall);
                    }
                }
            }

            for (String template : overrideMatchTemplates) {
                NodeList childNamedTemplateCalls = (NodeList) xp.evaluate("/stylesheet/template[@match='"+template+"']//call-template", overrideXsl, XPathConstants.NODESET);
                for (int i = 0; i < childNamedTemplateCalls.getLength(); i++) {
                    String templateToCall = childNamedTemplateCalls.item(i).getAttributes().getNamedItem("name").getTextContent();
                    if (!overrideNamedTemplates.contains(templateToCall)) {
                        desiredNamedTemplates.add(templateToCall);
                    }
                }
            }

            for (String matchTemplate : desiredMatchTemplates) {
                System.out.println(matchTemplate);
            }

            System.out.println("========");

            for (String namedTemplate : desiredNamedTemplates) {
                System.out.println(namedTemplate);
            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }
}
