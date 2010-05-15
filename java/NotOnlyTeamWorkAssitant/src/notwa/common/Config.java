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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
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
    private List<Node> rawConnections;
    private Document dom;
    private List<Node> rawApplicationsSettings;
    private final String CONFIG_FILE_NAME = "./notwa.config";
    private final XPath xpath = XPathFactory.newInstance().newXPath();
    
    /**
     * Hidden constructor to prevent instationing the class from outside world.
     */
    protected Config() {
        File configFile = new File(CONFIG_FILE_NAME);
        this.rawConnections = new ArrayList<Node>();
        this.rawApplicationsSettings = new ArrayList<Node>();

        try {
            this.parse(configFile);
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
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
     * Every conneection information is wrapped into the <code>ConnectionInfo</code>
     * instance and all these instances are kept inside single <code>Collection</code>.
     *
     * @return <code>Collection</code> of all <code>ConnectionInfo</code>.
     */
    public Collection<ConnectionInfo> getConnecionStrings() {
        Set<ConnectionInfo> connections = new TreeSet<ConnectionInfo>();
        for (Node con : rawConnections) {
            connections.add(parseConnectionInfo(con));
        }

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
        return parseApplicationSettings();
    }

    /**
     * Updates the <code>ApplicationSettings</code> maintained by the config
     * file.
     * <note>The changes will be promoted into the physical file as soon as you
     * call {@link #save()}.</note>
     *
     * @param as The <code>ApplicationSettings</code> you want to update.
     */
    public void setApplicationsSettings(ApplicationSettings as) {
        for (Node appSetting : rawApplicationsSettings) {
            if (appSetting.getNodeName().equals("Skin")) {
                setSkin(appSetting, as.getSkin());
            }
        }
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
            this.rawConnections = getChildNodesByPath(dom.getDocumentElement(), "./AvailableDatabases/Database");
            this.rawApplicationsSettings = getChildNodesByPath(dom.getDocumentElement(), "./ApplicationSettings/*");
        } else {
            throw new Exception("Config file does not exists!");
        }
    }

    /**
     * Saves the current state of <code>Config</code> to the physical file.
     */
    public void save() {
        try {
            Source source = new DOMSource(dom);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, new StreamResult(new File(CONFIG_FILE_NAME)));
        } catch (Exception ex) {
            LoggingFacade.handleException(ex);
        }
    }

    /**
     * Iterates through all settings found under ApplicationSettings element and
     * attempts to parse them, if they are known.
     * 
     * @return Parsed <code>ApplicationSettings</code> instance.
     */
    private ApplicationSettings parseApplicationSettings() {
        ApplicationSettings as = new ApplicationSettings();
        for (Node appSetting : rawApplicationsSettings) {
            if (appSetting.getNodeName().equals("Skin")) {
                as.setSkin(parseSkin(appSetting));
            } else {
                LoggingFacade.getLogger().logDebug("Unknown application setting %s", appSetting.getNodeName());
            }
        }
        
        return as;
    }

    /**
     * Attempts to parse the application setting node claimed to contain the
     * L&F skin class name.
     *
     * @param rawSkin The node to contain the L&F skin class name.
     * @return The L&F skin class name.
     */
    private String parseSkin(Node rawSkin) {
        String skinClassName = "";

        try {
            skinClassName = xpath.evaluate("./@name", rawSkin);
        } catch (XPathExpressionException xpeex) {
            LoggingFacade.handleException(xpeex);
        }

        return skinClassName;
    }

    /**
     * Parses out all connection information from the provided <code>Node</code>
     * utilizing the {@link XPath}.
     *
     * @param rawCon The node containing all the connection information.
     * @return The instance of <code>ConnectionInfo</code>
     */
    private ConnectionInfo parseConnectionInfo(Node rawCon) {
        ConnectionInfo ci = new ConnectionInfo();

        try {
            ci.setDbname(xpath.evaluate("./@dbname", rawCon));
            ci.setHost(xpath.evaluate("./@host", rawCon));
            ci.setUser(xpath.evaluate("./@user", rawCon));
            ci.setPort(xpath.evaluate("./@port", rawCon));
            ci.setPassword(xpath.evaluate("./@password", rawCon));
            ci.setLabel(xpath.evaluate("./@label", rawCon));
        } catch (XPathExpressionException xpeex) {
            LoggingFacade.handleException(xpeex);
            return null;
        }

        return ci;
    }

    /**
     * Sets the proper attribute of the node containing the Look & Feel skin
     * class name.
     * 
     * @param appSetting The node to be properly updated.
     * @param skin The L&F class name to be stored.
     */
    private void setSkin(Node appSetting, String skin) {
        Node skinNameNode = appSetting.getAttributes().getNamedItem("name");

        if (skinNameNode != null) {
            skinNameNode.setTextContent(skin);
        } else {
            LoggingFacade.getLogger().logDebug("Unable to update the application skin!");
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
}
