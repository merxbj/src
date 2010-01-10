package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();

        // Mebik lands collection
        int [] landCounts = {244,244,224,215,201,216,192,168,145,117,96,53,37};


        // Jirka Fric lands collection
        //int [] landCounts = {101,80,70,75,64,58,50,48,36,28,24,12,4};

        gas.createCustomizedLandList(landCounts);
        //gas.setTargetIncome(6396000.0);
        gas.setTargetIncome(3000000.0);
        gas.setStartingAmount(129490);
        gas.run();
    }

}
