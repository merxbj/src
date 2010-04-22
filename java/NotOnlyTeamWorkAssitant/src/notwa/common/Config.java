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
import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    private Set<ConnectionInfo> connections;
    private File configFile;
    private final String CONFIG_FILE_NAME = "./notwa.config";
    
    /**
     * Hidden constructor to prevent instationing the class from outside world.
     */
    protected Config() {
        this.configFile = new File(CONFIG_FILE_NAME);
        this.connections = new TreeSet<ConnectionInfo>();
        try {
            this.parse();
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
     * Gets all connection information parsed from the configuration file
     * Every conneection information is wrapped into the <code>ConnectionInfo</code>
     * instance and all these instances are kept inside single <code>Collection</code>.
     *
     * @return <code>Collection</code> of all <code>ConnectionInfo</code>.
     */
    public Collection<ConnectionInfo> getConnecionStrings() {
        return connections;

    }

    /**
     * Parse the XML configuration document utilizing the DOM {@link Document}.
     * 
     * @throws Exception If the config file does not exist.
     */
    public void parse() throws Exception {
        if (configFile.exists()) {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = db.parse(configFile);
            parseConnecionInformations(dom.getElementsByTagName("Database"));
        } else {
            throw new Exception("Config file does not exists!");
        }
    }

    /**
     * Parses out all connection informations from the provided <code>NodeList</code>
     * utilizing the {@link XPath} and adding them into the <code>Collection</code>
     * implemented as <code>Set</code>.
     *
     * @param odes XML nodes containing the actual connection information
     * @throws Exception If an error occures during the XPath evaluation
     */
    private void parseConnecionInformations(NodeList nodes) throws Exception {
        XPath xp = XPathFactory.newInstance().newXPath();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            ConnectionInfo ci = new ConnectionInfo();
            ci.setDbname(xp.evaluate("./@dbname", n));
            ci.setHost(xp.evaluate("./@host", n));
            ci.setUser(xp.evaluate("./@user", n));
            ci.setPort(xp.evaluate("./@port", n));
            ci.setPassword(xp.evaluate("./@password", n));
            ci.setLabel(xp.evaluate("./@label", n));
            connections.add(ci);
        }
    }
}
