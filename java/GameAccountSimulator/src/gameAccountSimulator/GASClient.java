package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();

        // Mebik lands collection
        int [] landCounts = {150,150,137,133,123,132,118,102,88,70,57,31,21};


        // Jirka Fric lands collection
        //int [] landCounts = {101,80,70,75,64,58,50,48,36,28,24,12,4};

        gas.createCustomizedLandList(landCounts);
        gas.setTargetIncome(6396000.0);
        gas.run();
    }

}
