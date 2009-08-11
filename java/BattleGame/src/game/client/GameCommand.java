package game.client;

import java.util.*;

public class GameCommand {

    public GameCommand() {
        rawName = "";
        rawParameters = new ArrayList<String>();
        parameters = new ArrayList<Object>();
        paramCount = 0;
        type = CommandType.NOTHING;
    }

    public boolean parse(String rawCommand) {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");

        if (tokenizer.hasMoreTokens()) {
            rawName = tokenizer.nextToken(); // first token is a commmand

            while (tokenizer.hasMoreTokens()) {
                rawParameters.add(tokenizer.nextToken());
            }
        }

        assignCommandType();

        return buildCommand();
    }

    public void clear() {
        rawName = "";
        rawParameters.clear();
        paramCount = 0;
        parameters.clear();
        type = CommandType.NOTHING;
    }

    private void assignCommandType() {

        if (rawName.compareTo("connect") == 0) {
            type = CommandType.CONNECT;
        } else if (rawName.compareTo("disconnect") == 0) {
            type = CommandType.DISCONNECT;
        } else if (rawName.compareTo("ping") == 0) {
            type = CommandType.PING;
        } else if ((rawName.compareTo("quit") == 0) || (rawName.compareTo("exit") == 0)) {
            type = CommandType.QUIT;
        }

    }

    private boolean buildConnnectParams() {
        String addr, port;
        StringTokenizer tokenizer;

        if (rawParameters.size() > 0) {
            tokenizer = new StringTokenizer(rawParameters.get(0), ":");
            if (tokenizer.hasMoreTokens()) {
                addr = tokenizer.nextToken();
                parameters.add(addr);
                if (tokenizer.hasMoreTokens()) {
                    port = tokenizer.nextToken();
                    parameters.add(port);
                }
            } else {
                type = CommandType.NOTHING;
                return false; // we have got no address!!
            }
        } else {
            type = CommandType.NOTHING;
            return false; // we have even got no parameters!
        }

        paramCount = parameters.size();
        return true;
    }

    private boolean buildCommand() {
        switch (type) {
            case CONNECT:
                return buildConnnectParams();
            case NOTHING:
                return false;
            default:
                return true;
        }
    }

    private String rawName;
    private ArrayList<String> rawParameters;
    public int paramCount;
    public ArrayList<Object> parameters;
    public CommandType type;

    public enum CommandType {
        NOTHING, CONNECT, DISCONNECT, PING, QUIT
    }
}
