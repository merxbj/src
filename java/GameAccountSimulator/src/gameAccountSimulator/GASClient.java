package gameAccountSimulator;

public class GASClient {

    public static void main(String[] args) {

        CommandLine cl = parseCommandLine(args);
        ConfigFile cf = new ConfigFile(cl.configPath);
        GameAccountSimulator gas = new GameAccountSimulator();

        try {
            cf.parse();
            gas.createCustomizedLandList(cf.getLandCounts());
            gas.setTargetIncome(cf.getTargetIncome());
            gas.addEndingConditionType(EndingConditionType.TARGET_INCOME);
            gas.setStartingAmount(cf.getStartingAmount());
            if (cl.cyclesCount != Integer.MAX_VALUE) {
                gas.addEndingConditionType(EndingConditionType.CYCLES_PASSED);
                gas.setTargetCyclesCount(cl.cyclesCount);
            }
            gas.run();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    private static void handleException(Exception ex) {
        System.out.println(String.format("Exception occured! %s", ex.toString()));
    }

    private static CommandLine parseCommandLine(String[] args) {
        CommandLine cl = new CommandLine();
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-config")) {
                    cl.configPath = args[i+1];
                } else if (args[i].equals("-c")) {
                    cl.cyclesCount = Integer.parseInt(args[i+1]);
                }
            }
        } catch (Exception ex) {
            handleException(ex);
        }
        
        return cl;
    }

    private static class CommandLine {
        int cyclesCount = Integer.MAX_VALUE;
        String configPath = "C:\\temp\\GASConfig.xml";
    }
}
