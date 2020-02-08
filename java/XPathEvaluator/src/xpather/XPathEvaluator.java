package xpather;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;

public class XPathEvaluator {
    private Document dom;
    private XPath xp;
    private Shell shell;

    public XPathEvaluator(CommandLine cl) throws Exception {
        this.dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(cl.getFileName()));
        this.xp = XPathFactory.newInstance().newXPath();
        this.shell = Shell.getInstance();
    }

    public void evaluate(Command cmd) throws Exception {
        NodeList result = (NodeList) xp.evaluate(cmd.getCommand(), dom, XPathConstants.NODESET);
        if (cmd.hasParameter("distinct")) {
            shell.printResults(new DistinctResults(result));
        } else if (cmd.hasParameter("count")) {
            shell.printResults(new CountResults(result));
        } else {
            shell.printResults(new SimpleResults(result));
        }
    }
}
