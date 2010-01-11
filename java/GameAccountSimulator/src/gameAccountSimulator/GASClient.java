package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();

        // Mebik lands collection
        int [] landCounts = {246,246,224,216,201,217,195,169,146,118,97,54,39};


        // Jirka Fric lands collection
        //int [] landCounts = {101,80,70,75,64,58,50,48,36,28,24,12,4};

        gas.createCustomizedLandList(landCounts);
        //gas.setTargetIncome(6396000.0);
        gas.setTargetIncome(3000000.0);
        gas.setStartingAmount(210845);
        gas.run();
    }

}
