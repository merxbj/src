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
public class ExcerciseThree {
    public static void main(String[] args) throws Exception {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document source = builder.parse(new File("C:\\Users\\eTeR\\Documents\\cvut\\ad7b36xml\\u6\\ad7b36.xml"));
        
        Document output = buildTaskDaySummary(source, builder);
        
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(new DOMSource(output), new StreamResult(System.out));
    }

    private static Document buildTaskDaySummary(Document source, DocumentBuilder builder) {
        Map<String,Task> tasks = new HashMap<String,Task>();
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
                        String estimateRaw = taskNode.getAttributes().getNamedItem("estimate").getNodeValue();
                        double estimate = 0.0;
                        if (estimateRaw != null && !estimateRaw.equals("")) {
                            estimate = Double.parseDouble(estimateRaw);
                        }
                        double correction = Double.NaN;
                        Node correctionNode = taskNode.getAttributes().getNamedItem("estimate");
                        if (correctionNode != null) {
                            String correctionRaw = correctionNode.getNodeValue();
                            if (correctionRaw != null && !correctionRaw.equals("")) {
                                correction = Double.parseDouble(correctionRaw);
                            }
                        }
                        
                        tasks.put(id, new Task(id, name, estimate, correction));
                    }
                }
            }
        }
        
        for (int i = 0; i < rootChildNodes.getLength(); i++) {
            Node node = rootChildNodes.item(i);
            if (node.getNodeName().equals("WeekSummary")) {
                NodeList dayNodes = node.getChildNodes();
                for (int j = 0; j < dayNodes.getLength(); j++) {
                    Node dayNode = dayNodes.item(j);
                    if (dayNode.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList taskRefNodes = dayNode.getChildNodes();
                        for (int k = 0; k < taskRefNodes.getLength(); k++) {
                            Node taskRefNode = taskRefNodes.item(k);
                            if (taskRefNode.getNodeType() == Node.ELEMENT_NODE) {
                                String taskId = taskRefNode.getAttributes().getNamedItem("id").getNodeValue();
                                String taskRefHoursRaw = taskRefNode.getAttributes().getNamedItem("hours").getNodeValue();
                                if (taskRefHoursRaw != null && !taskRefHoursRaw.equals("")) {
                                    double taskRefHours = Double.parseDouble(taskRefHoursRaw);
                                    if (taskRefHours > 0) {
                                        Task task = tasks.get(taskId);
                                        if (task != null) {
                                            Day day = null;
                                            String dayName = dayNode.getAttributes().getNamedItem("dayName").getNodeValue();
                                            if (task.days.containsKey(dayName)) {
                                                day = task.days.get(dayName);
                                            } else {
                                                day = new Day(dayName, 0);
                                                task.days.put(dayName, day);
                                            }
                                            day.hours += taskRefHours;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Document output = builder.newDocument();
        Element workSummaryElement = (Element) output.appendChild(output.createElement("WorkSummary"));
        
        Element taskDefinitionsElement = (Element) workSummaryElement.appendChild(output.createElement("TaskDefinitions"));
        for (Task task : tasks.values()) {
            Element taskElement = (Element) taskDefinitionsElement.appendChild(output.createElement("Task"));
            Attr idAttribute = output.createAttribute("id");
            taskElement.setAttributeNode(idAttribute);
            idAttribute.setValue(task.id);
            
            Attr nameAttribute = output.createAttribute("name");
            taskElement.setAttributeNode(nameAttribute);
            nameAttribute.setValue(task.name);
            
            Attr estimateAttribute = output.createAttribute("estimate");
            taskElement.setAttributeNode(estimateAttribute);
            estimateAttribute.setValue(String.format("%.0f", task.estimate));
            
            if (task.correction != Double.NaN)  {
                Attr correctionAttribute = output.createAttribute("correction");
                taskElement.setAttributeNode(correctionAttribute);
                correctionAttribute.setValue(String.format("%.0f", task.correction));
            }
        }
        
        Element taskSummaryElement = (Element) workSummaryElement.appendChild(output.createElement("TaskSummary"));
        for (Task task : tasks.values()) {
            Element taskRefElement = output.createElement("TaskRef");
            Attr idAttribute = output.createAttribute("id");
            taskRefElement.setAttributeNode(idAttribute);
            idAttribute.setValue(task.id);
            double totalHours = 0.0;
            for (Day day : task.days.values()) {
                if (day.hours > 0) {
                    Element dayElement = (Element) taskRefElement.appendChild(output.createElement("Day"));
                    Attr dayNameAttribute = output.createAttribute("dayName");
                    dayElement.setAttributeNode(dayNameAttribute);
                    dayNameAttribute.setValue(day.dayName);
                    
                    Attr hoursAttribute = output.createAttribute("hours");
                    dayElement.setAttributeNode(hoursAttribute);
                    hoursAttribute.setValue(String.format("%.0f", day.hours));
                    
                    totalHours += day.hours;
                }
            }
            
            if (totalHours > 0.0) {
                taskSummaryElement.appendChild(taskRefElement);
            }
        }
        
        return output;
    }
    
    private static class Day implements Comparable<Day> {
        public String dayName;
        public double hours;

        public Day(String dayName, double hours) {
            this.dayName = dayName;
            this.hours = hours;
        }

        @Override
        public int compareTo(Day o) {
            return dayName.compareTo(o.dayName);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Day other = (Day) obj;
            if ((this.dayName == null) ? (other.dayName != null) : !this.dayName.equals(other.dayName)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (this.dayName != null ? this.dayName.hashCode() : 0);
            return hash;
        }
        
    }
    
    private static class Task implements Comparable<Task> {
        public String id;
        public double estimate;
        public double correction;
        public String name;
        public Map<String,Day> days;

        public Task(String id, String name, double estimate, double correction) {
            this.id = id;
            this.name = name;
            this.days = new HashMap<String,Day>();
            this.correction = correction;
            this.estimate = estimate;
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
