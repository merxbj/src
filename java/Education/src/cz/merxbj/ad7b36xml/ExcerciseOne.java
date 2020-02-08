/*
 * u1
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
package cz.merxbj.ad7b36xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class ExcerciseOne {
    public static void main(String[] args) throws Exception {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document source = builder.parse(new File("C:\\Users\\eTeR\\Documents\\cvut\\ad7b36xml\\u6\\ad7b36.xml"));
        
        Document output = spentMostTime(source, builder);
        
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(new DOMSource(output), new StreamResult(System.out));
    }

    private static Document spentMostTime(Document source, DocumentBuilder builder) {
        Map<String,Task> hours = new HashMap<String,Task>();
        Element root = source.getDocumentElement();
        NodeList rootChildNodes = root.getChildNodes();
        for (int i = 0; i < rootChildNodes.getLength(); i++) {
            Node node = rootChildNodes.item(i);
            if (node.getNodeName().equals("TaskDefinitions")) {
                NodeList taskNodes = node.getChildNodes();
                for (int j = 0; j < taskNodes.getLength(); j++) {
                    Node taskNode = taskNodes.item(j);
                    if (taskNode.getNodeType() == Node.ELEMENT_NODE) {
                        String id = taskNode.getAttributes().getNamedItem("id").getNodeValue();
                        String name = taskNode.getAttributes().getNamedItem("name").getNodeValue();
                        hours.put(id, new Task(id, name));
                    }
                }
            }
        }
        
        for (int i = 0; i < rootChildNodes.getLength(); i++) {
            Node node = rootChildNodes.item(i);
            if (node.getNodeName().equals("WeekSummary")) {
                NodeList dayNodes = node.getChildNodes();
                for (int j = 0; j < dayNodes.getLength(); j++) {
                    NodeList taskRefNodes = dayNodes.item(j).getChildNodes();
                    for (int k = 0; k < taskRefNodes.getLength(); k++) {
                        Node taskRefNode = taskRefNodes.item(k);
                        if (taskRefNode.getNodeType() == Node.ELEMENT_NODE) {
                            String taskId = taskRefNode.getAttributes().getNamedItem("id").getNodeValue();
                            String taskRefHoursRaw = taskRefNode.getAttributes().getNamedItem("hours").getNodeValue();
                            if (taskRefHoursRaw != null && !taskRefHoursRaw.equals("")) {
                                double taskRefHours = Double.parseDouble(taskRefHoursRaw);
                                if (taskRefHours > 0) {
                                    Task task = hours.get(taskId);
                                    if (task != null) {
                                        task.totalHours += taskRefHours;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        double maxTotalHours = 0;
        for (Task task : hours.values()) {
            if (task.totalHours > maxTotalHours) {
                maxTotalHours = task.totalHours;
            }
        }
        
        Document output = builder.newDocument();
        Element spentMostTimeElement = (Element) output.appendChild(output.createElement("SpentMostTime"));
        
        for (Task task : hours.values()) {
            if (task.totalHours == maxTotalHours) {
                Element taskInfoElement = (Element) spentMostTimeElement.appendChild(output.createElement("TaskInfo"));
                Element taskNameElement = (Element) taskInfoElement.appendChild(output.createElement("TaskName"));
                Element spentElement = (Element) taskInfoElement.appendChild(output.createElement("Spent"));
                taskNameElement.setTextContent(task.name);
                spentElement.setTextContent(String.format("%.0f", task.totalHours));
            }
        }
        
        return output;
    }
    
    private static class Task implements Comparable<Task> {
        public String id;
        public String name;
        public double totalHours;

        public Task(String id, String name) {
            this.id = id;
            this.name = name;
            this.totalHours = 0.0;
        }

        @Override
        public int compareTo(Task o) {
            return this.id.compareTo(o.id);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Task other = (Task) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}
