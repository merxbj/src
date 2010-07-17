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
        Node dbname = node.getAttributes().getNamedItem("dbname");
        Node host = node.getAttributes().getNamedItem("host");
        Node user = node.getAttributes().getNamedItem("user");
        Node port = node.getAttributes().getNamedItem("port");
        Node password = node.getAttributes().getNamedItem("password");
        Node label = node.getAttributes().getNamedItem("label");
        Node notwaLogin = node.getAttributes().getNamedItem("notwaLogin");
        
        super.setDbname(dbname != null ? dbname.getNodeValue() : "");
        super.setHost(host != null ? host.getNodeValue() : "");
        super.setUser(user != null ? user.getNodeValue() : "");
        super.setPort(port != null ? port.getNodeValue() : "");
        super.setPassword(password != null ? password.getNodeValue() : "");
        super.setLabel(label != null ? label.getNodeValue() : "");
        this.setNotwaUserName(notwaLogin != null ? notwaLogin.getNodeValue() : "");
        
        return this;
    }
}
