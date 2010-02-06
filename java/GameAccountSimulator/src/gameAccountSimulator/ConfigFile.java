package gameAccountSimulator;

import java.io.*;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import javax.xml.xpath.*;

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
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = db.parse(configFile);

            XPath xp = XPathFactory.newInstance().newXPath();
            parseLandCollectionSetUp(xp.evaluate("//GASConfiguration/LandCollectionSetUp", dom));
            parseTargetIncome(xp.evaluate("//GASConfiguration/TargetIncome", dom));
            parseStartingAmount(xp.evaluate("//GASConfiguration/StartingAmount", dom));

        } else {
            throw new Exception("Config file does not exists!");
        }
    }

    private void parseLandCollectionSetUp(String lcs) {       
        StringTokenizer st = new StringTokenizer(lcs, ",");
        landCounts = new int[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
            landCounts[i] = Integer.parseInt(st.nextToken());
        }
    }

    private void parseTargetIncome(String ti) {
        targetIncome = Double.parseDouble(ti);
    }

    private void parseStartingAmount(String sa) {
        startingAmount = Double.parseDouble(sa);
    }

    private int[] landCounts;
    private double targetIncome;
    private double startingAmount;
    private File configFile;
}
