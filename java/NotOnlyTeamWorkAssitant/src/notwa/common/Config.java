package notwa.common;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import notwa.common.ExceptionHandler;

public class Config {

	private static Config singleton;
	private final String CONFIG_FILE_NAME = "./notwa.config";
	private ExceptionHandler eh;
	
	public static Config getInstance() {
		if (singleton == null)
			singleton = new Config();
		return singleton;
	}

	protected Config() {
        this.configFile = new File(CONFIG_FILE_NAME);
        this.eh = ExceptionHandler.getInstanece();
        try {
        	this.parse();
        } catch (Exception ex) {
        	eh.handleException(ex);
        }
    }

    public String[] getConnecionStrings() {
        return connecionStrings;
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

    private void parseConnecionStrings(NodeList nodes) {       
        
    }

    private String[] connecionStrings;
    private File configFile;
}
