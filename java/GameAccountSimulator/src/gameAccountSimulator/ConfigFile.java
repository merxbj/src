package gameAccountSimulator;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.StringTokenizer;

public class ConfigFile {

    public ConfigFile(String configFilePath) {
        this.configFile = new File(configFilePath);
    }

    public int[] getLandCounts() {
        return landCounts;
    }

    public double getStartingAmount() {
        return startingAmount;
    }

    public double getTargetIncome() {
        return targetIncome;
    }

    public void parse() throws Exception {
        if (configFile.exists()) {
            XMLInputFactory f = XMLInputFactory.newInstance();
            XMLStreamReader r = f.createXMLStreamReader(new FileReader(configFile));

            while (r.hasNext()) {
                if (r.getEventType() == XMLEvent.START_ELEMENT && r.getLocalName().compareTo("GASConfiguration") == 0) {
                    parseGASConfiguration(r);
                }
                r.next();
            }
        } else {
            throw new Exception("Config file does not exists!");
        }
    }

    private void parseGASConfiguration(XMLStreamReader r) throws Exception {
        while (r.hasNext()) {
            if (r.getEventType() == XMLEvent.START_ELEMENT) {
                if (r.getLocalName().compareTo("LandCollectionSetUp") == 0)
                    parseLandCollectionSetUp(r);
                else if (r.getLocalName().compareTo("TargetIncome") == 0)
                    parseTargetIncome(r);
                else if (r.getLocalName().compareTo("StartingAmount") == 0)
                    parseStartingAmount(r);
            }
            r.next();
        }
    }

    private void parseLandCollectionSetUp(XMLStreamReader r) throws Exception {
        while (r.hasNext() && r.getEventType() != XMLEvent.END_ELEMENT) {
            if (r.getEventType() == XMLEvent.CHARACTERS) {
                StringTokenizer st = new StringTokenizer(r.getText(), ",");
                landCounts = new int[st.countTokens()];
                for (int i = 0; st.hasMoreTokens(); i++) {
                    landCounts[i] = Integer.parseInt(st.nextToken());
                }
            }
            r.next();
        }
    }

    private void parseTargetIncome(XMLStreamReader r) throws Exception {
        while (r.hasNext() && r.getEventType() != XMLEvent.END_ELEMENT) {
            if (r.getEventType() == XMLEvent.CHARACTERS) {
                targetIncome = Double.parseDouble(r.getText());
            }
            r.next();
        }
    }

    private void parseStartingAmount(XMLStreamReader r) throws Exception {
        while (r.hasNext() && r.getEventType() != XMLEvent.END_ELEMENT) {
            if (r.getEventType() == XMLEvent.CHARACTERS) {
                startingAmount = Double.parseDouble(r.getText());
            }
            r.next();
        }
    }

    private int[] landCounts;
    private double targetIncome;
    private double startingAmount;
    private File configFile;
}
