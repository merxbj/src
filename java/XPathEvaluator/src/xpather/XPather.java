package xpather;

public class XPather {
    public static void main(String[] args) {
        Shell shell = Shell.getInstance();
        try {
            CommandLine cl = CommandLine.parse(args);
            XPathEvaluator engine = new XPathEvaluator(cl);
            Command cmd = new Command();

            while (shell.getNextCommand(cmd)) {
                try {
                    engine.evaluate(cmd);
                    cmd.clear();
                } catch (Exception ex) {
                    shell.handleException(ex);
                }
            }

        } catch (Exception ex) {
            shell.handleException(ex);
        }
    }
}
