/*
 * XmlHelpers
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

package ss.helpers;

import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import ss.application.CommandLine;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class XmlHelpers {

    public static void printNode(Node node) {
        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(new DOMSource(node), new StreamResult(System.out));
        } catch (Exception ex) {
            CommandLine.handleException(ex);
        }
    }

}
