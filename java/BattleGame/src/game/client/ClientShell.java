package game.client;

import java.util.*;
import java.net.*;

public class ClientShell {

    public ClientShell() {
        System.out.println("Welcome to Battle Game!");
    }

    public void log(String message) {
        System.out.println(String.format("\t%s", message));
    }

    public void handleException(Exception ex) {
        String message = translateException(ex);
        log(message);
    }

    public boolean requestCommand(GameCommand command) {
        Scanner scan = new Scanner(System.in);

        System.out.print("$ ");
        String buf = scan.nextLine();

        if (command.parse(buf)) {
            if (command.type.equals(GameCommand.CommandType.QUIT))
                return false;
        } else {
            log("Invalid command or command parameters!");
        }

        return true;
    }

    private String translateException(Exception ex) {
        
        /* This will be a default message in release! */
        //String message = "Unexpected errror has occured!";
        String message = ex.toString();

        if (ex instanceof UnknownHostException)
            message = "Requested addres is unreachable!";
        else if (ex instanceof SocketException)
            message = "There is no Battle Game server on the requested address!";

        return message;
    }

    /*
     * Game Client global constants
     */
    public final int maxConnectionRetries = 3;
}
