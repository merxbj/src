package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();

        // Mebik lands collection
        int [] landCounts = {253,253,232,225,209,225,202,174,152,122,100,55,40};


        // Jirka Fric lands collection
        //int [] landCounts = {101,80,70,75,64,58,50,48,36,28,24,12,4};

        gas.createCustomizedLandList(landCounts);
        //gas.setTargetIncome(6396000.0);
        gas.setTargetIncome(6396000.0);
        gas.setStartingAmount(3395228);
        gas.run();
    }

}
