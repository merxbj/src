/*
 * Config
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

package notwa.common;

import notwa.logger.LoggingFacade;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import org.w3c.dom.*;

/**
 * Config is the utility class for acquiring the data from configuration file.
 * Config is written and suposed to be used as a singleton to provide sole
 * instance operating over the single XML file stored in the hardcoded path in
 * the system.
 * <p>Even thought there is no motivation to have something written to the config
 * file, so there is no actual motivation to have this class as singleton,
 * it is discouraged to change this behavior as the need to persist any kind
 * of information here could arise in future. This would then may cause the
 * trouble.</p>
 *
 * @author  Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Config {

    private static Config instance;
    private Document dom;
    private ApplicationSettings as = new ApplicationSettings();
    private Set<NotwaConnectionInfo> connections = new TreeSet<NotwaConnectionInfo>();
    private static String configFilePath = "./notwa.config";
    private final XPath xpath = XPathFactory.newInstance().newXPath();
    
    /**
     * Hidden constructor to prevent instancing the class from outside world.
     */
    protected Config() {
        File configFile = new File(configFilePath);

        try {
            this.parse(configFile);
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
    }
    
    /**
     * Intended to use only during the application startup to specify the config
     * file path.
     *
     * @param path - The actual path to the config file.
     */
    public static void setConfigFilePath(String path) {
        configFilePath = path;
    }

    /**
     * Gets the sole instance of the Config class which is maintained as the
     * static singleton.
     * 
     * @return The singleton instance
     */
    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    /**
     * Gets all connection information parsed from the configuration file.
     * Every connection information is wrapped into the <code>ConnectionInfo</code>
     * instance and all these instances are kept inside single <code>Collection</code>.
     *
     * @return <code>Collection</code> of all <code>ConnectionInfo</code>.
     */
    public Collection<NotwaConnectionInfo> getConnecionStrings() {
        return connections;
    }

    /**
     * Gets the <code>ApplicationSettings</code> parsed from the configuration file.
     *
     * <note>If you change them and want you to save them to the physical file
     * during next {@link #save()} call, use
     * {@link #setApplicationsSettings(notwa.common.ApplicationSettings) </note>
     * to update them.
     *
     * @return The parsed <code>ApplicationSettings</code>.
     */
    public ApplicationSettings getApplicationSettings() {
        return as;
    }

    public void setApplicationsSettings(ApplicationSettings as) {
        this.as = as;
    }

    public void setConnectionInfo(NotwaConnectionInfo nci) {
        connections.add(nci);
    }
    
    /**
     * Parse the XML configuration document utilizing the DOM {@link Document}.
     *
     * @param configFile The file claimed to be the configuration XML file.
     * @throws Exception If the config file does not exist.
     */
    public void parse(File configFile) throws Exception {
        if (configFile.exists()) {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.dom = db.parse(configFile);
            
            for (Node node : getChildNodesByPath(dom.getDocumentElement(), "./*")) {
                if (node.getNodeName().equals("ApplicationSettings")) {
                    as.parseFromConfig(node);
                }
                else if (node.getNodeName().equals("AvailableDatabases")) {
                    for (int i=0; i<node.getChildNodes().getLength(); i++) {
                        Node subNode = node.getChildNodes().item(i);
                        if (subNode.getNodeName().equals("Database")) {
                            NotwaConnectionInfo nci = new NotwaConnectionInfo();
                            nci.parseFromConfig(subNode);
                            connections.add(nci);
                        }
                    }
                }
            }
        } else {
            throw new Exception("Config file does not exists!");
        }
    }

    /**
     * Saves the current state of <code>Config</code> to the physical file.
     */
    public void save() {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
    
            /*
             * NotwaConfiguration
             */
            Element notwaConfigurationElement = doc.createElement("NotwaConfiguration");
            doc.appendChild(notwaConfigurationElement);
    
            /*
             * ApplicationSettings
             */
            Element appSettings = doc.createElement("ApplicationSettings");
            notwaConfigurationElement.appendChild(appSettings);
            
            /*
             * ApplicationSettings - childs
             */
            Element skin = doc.createElement("Skin");
            skin.setAttribute("name", as.getSkin());
            
            Element rememberLogin = doc.createElement("RememberLogin");
            rememberLogin.setAttribute("remember", as.isRememberNotwaLogin() ? "1" : "0");
            
            appSettings.appendChild(skin);
            appSettings.appendChild(rememberLogin);
    
            /*
             * AvailableDatabases 
             */
            Element availableDatabases = doc.createElement("AvailableDatabases");
            notwaConfigurationElement.appendChild(availableDatabases);
    
            /*
             * AvailableDatabases - childs 
             */
            for (NotwaConnectionInfo nci : connections) {
                Element database = doc.createElement("Database");
    
                database.setAttribute("label", nci.getLabel());
                database.setAttribute("dbname", nci.getDbname());
                database.setAttribute("host", nci.getHost());
                database.setAttribute("port", nci.getPort());
                database.setAttribute("user", nci.getUser());
                database.setAttribute("password", nci.getPassword());
                database.setAttribute("notwaLogin", nci.getNotwaUserName());
                
                availableDatabases.appendChild(database);
            }
            
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","3");

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
    
            File configFile = new File(configFilePath);
            if (configFile.delete() || !configFile.exists()) {
                FileWriter fw = new FileWriter(configFile, true);
                fw.append(sw.toString());
                fw.close();
            }
            else {
                throw new Exception("Config file cannot be deleted!");
            }
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
    }

    /**
     * Finds all the child elements of given parent matching the xpath provided.
     *
     * @param parent The Node where to start.
     * @param path The path to be evaluated.
     * @return The <code>List</code> of all the child nodes matching the given xpath.
     */
    private List<Node> getChildNodesByPath(Node parent, String path) {
        List<Node> childs = new ArrayList<Node>();
        try {
            NodeList rawChilds = (NodeList) xpath.evaluate(path, parent, XPathConstants.NODESET);
            for (int i = 0; i < rawChilds.getLength(); i++) {
                childs.add(rawChilds.item(i));
            }
        } catch (XPathExpressionException xpeex) {
            LoggingFacade.handleException(xpeex);
        }

        return childs;
    }
    
    /**
     * This functions is needed when is unsure if config hasnt been modified
     */
    public void reloadConfig() {
        /*
         * Clear all already filled data
         */
        connections.clear();
        
        
        File configFile = new File(configFilePath);

        try {
            this.parse(configFile);
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
    }
}
