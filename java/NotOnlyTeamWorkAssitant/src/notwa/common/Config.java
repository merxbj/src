package notwa.common;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import notwa.common.LoggingInterface;
import java.util.TreeSet;
import java.util.Set;

public class Config {

	private static Config singleton;
	private final String CONFIG_FILE_NAME = "./notwa.config";
	
	public static Config getInstance() {
		if (singleton == null)
			singleton = new Config();
		return singleton;
	}

	protected Config() {
        this.configFile = new File(CONFIG_FILE_NAME);
        this.connections = new TreeSet<ConnectionInfo>();
        try {
        	this.parse();
        } catch (Exception ex) {
        	LoggingInterface.getInstanece().handleException(ex);
        }
    }

    public ConnectionInfo[] getConnecionStrings() {
        return (ConnectionInfo[]) connections.toArray();
    }

    public void parse() throws Exception {
        if (configFile.exists()) {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = db.parse(configFile);
            parseConnecionStrings(dom.getElementsByTagName("connection"));
        } else {
            throw new Exception("Config file does not exists!");
        }
    }

    private void parseConnecionStrings(NodeList nodes) throws Exception {       
        XPath xp = XPathFactory.newInstance().newXPath();
    	for (int i = 0; i < nodes.getLength(); i++) {
        	Node n = nodes.item(i);
        	ConnectionInfo ci = new ConnectionInfo();
        	ci.setDbname(xp.evaluate("/@dbname", n));
        	ci.setHost(xp.evaluate("/@host", n));
        	ci.setUser(xp.evaluate("/@user", n));
        	ci.setPort(xp.evaluate("/@port", n));
        	ci.setPassword(xp.evaluate("/@password", n));
        	ci.setLabel(xp.evaluate("/@label", n));
        	connections.add(ci);
        }
    }

    private Set<ConnectionInfo> connections;
    private File configFile;
}
