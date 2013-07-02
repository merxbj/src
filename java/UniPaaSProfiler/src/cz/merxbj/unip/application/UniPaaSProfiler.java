package cz.merxbj.unip.application;

import cz.merxbj.unip.common.CommonStatics;
import javax.swing.UIManager;

/**
 *
 * @author tomas
 */
public class UniPaaSProfiler {
    public static String appName = "UniPaaS Profiler";
    public static String version = "1.0.0";
    public static String description = "This application has been created for loading UniPaaS log files to JTreeTable, that should make it more clear.";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String logFilePath = "";
        trySetLookAndFeel();
        
        if (hasArguments(args)) {
            logFilePath = args[0];
        }
        
        UniPaaSProfilerViewThread thread = new UniPaaSProfilerViewThread(logFilePath);
        
        java.awt.EventQueue.invokeLater(thread);
    }

    private static boolean hasArguments(String[] args) {
        return args.length > 0;
    }

    public static void trySetLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            CommonStatics.invokeErrorDialog("There was an error while loading default look and feel. Let java to choose.");
        }
    }
}
