package game.client;

import java.util.*;

public class GameCommand {

    public GameCommand() {
        commandName = "";
        commandParameters = "";
    }

    public boolean parse(String rawCommand) {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");

    }

    private String commandName;
    private String commandParameters;
}
