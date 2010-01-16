package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();
        ConfigFile cf = new ConfigFile("C:\\temp\\GASConfig.xml");

        try {
            cf.parse();
            gas.createCustomizedLandList(cf.getLandCounts());
            gas.setTargetIncome(cf.getTargetIncome());
            gas.setStartingAmount(cf.getStartingAmount());
            gas.run();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    public static void handleException(Exception ex) {
        System.out.println(String.format("Invalid or not-existing config file! %s", ex.toString()));
    }

}
