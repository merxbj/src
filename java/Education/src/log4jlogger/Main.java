/*
 * Main
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

package log4jlogger;

import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Main {
    public static void main(String[] args) {
        try {
            Document config = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("app.config");
            Element log4jConfig = (Element) config.getDocumentElement().getElementsByTagName("log4j:configuration").item(0);
            DOMConfigurator.configure(log4jConfig);
            Logger log = LogManager.getLogger("Test");

            log.debug("Nastala chyba, kterou je potreba zalogovat!");
            log.fatal("Chyba!", new IndexOutOfBoundsException("Mimo rozsah!"));

        } catch (Exception ex) {
        }
    }
}
