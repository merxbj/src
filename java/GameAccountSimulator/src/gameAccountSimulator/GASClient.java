package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        GameAccountSimulator gas = new GameAccountSimulator();
        ConfigFile cf = new ConfigFile(parseCommandLine(args));

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

    private static void handleException(Exception ex) {
        System.out.println(String.format("Invalid or not-existing config file! %s", ex.toString()));
    }

    private static String parseCommandLine(String[] args) {
        if (args.length == 1) {
            return args[0];
        } else {
            return new String("C:\\temp\\GASConfig.xml");
        }
    }
}
