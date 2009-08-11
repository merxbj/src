package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();

        // Mebik lands collection
        //int [] landCounts = {116,116,106,102,95,102,91,78,68,53,43,21};


        // Jirka Fric lands collection
        int [] landCounts = {101,80,70,75,64,58,50,48,36,28,24,12,4};

        gas.createCustomizedLandList(landCounts);
        gas.setTargetIncome(1006200.0);
        gas.run();
    }

}
