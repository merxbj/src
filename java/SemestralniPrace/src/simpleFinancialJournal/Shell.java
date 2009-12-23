/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleFinancialJournal;

import java.util.*;

/**
 *
 * @author eTeR
 */
public class Shell {

    public Shell() {
        System.out.println("Welcome to Simple Financial Journal!");
    }

    public void log(String message) {
        System.out.println(String.format("\t%s", message));
    }

    public void handleException(Exception ex) {
        String message = translateException(ex);
        log(message);
    }

    public void printHelp() {
        log("Please enter one of following commands:");
        log("help, assign, close, save, select, list, balance, sort, add, remove, create, quit, exit");
    }

    public boolean requestCommand(JournalCommand command) {
        Scanner scan = new Scanner(System.in);

        System.out.print("$ ");
        String buf = scan.nextLine();

        if (command.parse(buf)) {
            if (command.type.equals(JournalCommand.CommandType.QUIT))
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

        /*
        if (ex instanceof UnknownHostException)
            message = "Requested addres is unreachable!";
        else if (ex instanceof SocketException)
            message = "There is no Battle Game server on the requested address!";
         */

        return message;
    }
}
