/*
 * ConnectionInfo
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

import org.w3c.dom.Node;

public class NotwaConnectionInfo extends ConnectionInfo {
    private String notwaUserName;

    public void setNotwaUserName(String notwaUserName) {
        this.notwaUserName = notwaUserName;
    }
    public String getNotwaUserName() {
        return notwaUserName;
    }
    

    /**
     * Parses out all connection information from the provided <code>Node</code>
     * utilizing the {@link XPath}.
     *
     * @param rawCon The node containing all the connection information.
     * @return The instance of <code>ConnectionInfo</code>
     */
    public NotwaConnectionInfo parseFromConfig(Node node) {
        super.setDbname(node.getAttributes().getNamedItem("dbname").getTextContent());
        super.setHost(node.getAttributes().getNamedItem("host").getTextContent());
        super.setUser(node.getAttributes().getNamedItem("user").getTextContent());
        super.setPort(node.getAttributes().getNamedItem("port").getTextContent());
        super.setPassword(node.getAttributes().getNamedItem("password").getTextContent());
        super.setLabel(node.getAttributes().getNamedItem("label").getTextContent());
        this.setNotwaUserName(node.getAttributes().getNamedItem("notwaLogin").getTextContent());
        
        return this;
    }
}
