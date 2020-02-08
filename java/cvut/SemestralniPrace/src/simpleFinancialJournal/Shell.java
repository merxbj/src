package simpleFinancialJournal;

import java.util.*;

/**
 * Method providing an unified way for work with the command line.
 */
public class Shell {

    public Shell() {
        scanner = new Scanner(System.in);
        System.out.println("Welcome to Simple Financial Journal!");
    }

    /*
     * Writes the given message to the stdout.
     */
    public void log(String message) {
        System.out.println(String.format("\t%s", message));
    }

    /*
     * Writes the exception message to the stdout.
     */
    public void handleException(Exception ex) {
        String message = translateException(ex);
        log(message);
    }

    public void printHelp() {
        log("Please enter one of following commands:");
        log("help, assign, close, save, select, list, balance, sort, add, remove, update, create, quit, exit");
    }

    public boolean requestCommand(JournalCommand command) {
        System.out.print("$ ");
        String buf = scanner.nextLine();

        if (command.parse(buf)) {
            if (command.type.equals(JournalCommand.CommandType.QUIT)) {
                return false;
            }
        } else {
            log("Invalid command or command parameters!");
        }

        return true;
    }

    /*
     * Request user for input with given message by unified way.
     */
    public String requestUserInput(String message) {
        System.out.print(String.format("%s $ ", message));
        return scanner.nextLine();
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

    private Scanner scanner;
}
