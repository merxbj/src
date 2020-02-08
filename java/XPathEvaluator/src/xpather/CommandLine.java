package xpather;

import java.io.File;

public class CommandLine {
    
    private String fileName;

    private CommandLine() {
        fileName = "";
    }

    private CommandLine(String fileName) {
        this.fileName = fileName;
    }

    public static CommandLine parse (String[] args) throws Exception {
        final String addr = args[0];
        if (!(new File(addr).exists())) {
            throw new Exception("Supplied file does not exist!");
        }
        return new CommandLine(addr);
    }

    public String getFileName() {
        return fileName;
    }
}
