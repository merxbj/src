package game.common;

import java.io.*;

public class DataBearer implements Serializable{

    public DataBearer(String command, CommandType cmdType) {
        this.command = command;
        this.cmdType = cmdType;
    }

    public String command;
    public CommandType cmdType;
}
