package game.client;

import java.util.*;

public class GameShell {

    public void log(String message) {
        System.out.println(String.format("\t%s", message));
    }

    public void handleException(Exception ex) {
        log(ex.toString());
    }

    public boolean requestInput(String input) {
        Scanner scan = new Scanner(System.in);

        String buf = scan.nextLine();

        if (verifyCommand(buf)) {
            input = buf;
            return true;
        }

        return false;
    }

    private boolean verifyCommand(String command) {
        return true;
    }

}
