package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();

        // Mebik lands collection
        int [] landCounts = {78,78,71,69,64,69,60,52,44,34,27,13};


        // Jirka Fric lands collection
        //int [] landCounts = {101,80,70,75,64,58,50,48,36,28,24,12,4};

        gas.createCustomizedLandList(landCounts);
        gas.setTargetIncome(1006200.0);
        gas.run();
    }

}
