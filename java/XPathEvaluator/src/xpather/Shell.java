package xpather;

import java.util.Scanner;

public class Shell {
    private static Shell instance;
    private Scanner sc;

    private Shell() {
        sc = new Scanner(System.in);
    }

    public static Shell getInstance() {
        if (instance == null) {
            instance = new Shell();
        }
        return instance;
    }

    public boolean getNextCommand(Command cmd) {
        try {
            System.out.print("xpath$ ");
            String input = sc.nextLine();
            if (!input.equals(":q")) {
                if (input.startsWith("{")) {
                    String parameterSection = input.substring(1, input.indexOf("}"));
                    String[] parameters = parameterSection.split(";");
                    for (String parameter : parameters) {
                        cmd.addParameter(parameter);
                    }
                    input = input.substring(input.indexOf("}") + 1);
                }
                cmd.setQuery(input);
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Invalid command!");
            return true;
        }
        return false;
    }

    public void printResults(Iterable<String> res) {
        for (String result : res) {
            System.out.print(String.format("\t%s\n", result));
        }
        System.out.println("");
    }

    /**
     * Repors the caught <code>Exception</code> in a unified way. This means that
     * it logs the error message with string representation of the given exception.
     * This is followed by the stack trace and finally by the inner exception
     * which caused the given exception.
     *
     * @param ex <code>Exception<code> to be handled.
     */
    public void handleException(Throwable ex) {
        System.out.println(formatException(ex));
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    private String formatException(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString());
        sb.append("\n");
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("\t at ");
            sb.append(ste.toString());
            sb.append("\n");
        }

        Throwable innerException = ex.getCause();
        while (innerException != null) {
            sb.append("\t caused by ");
            sb.append(innerException.toString());
            sb.append("\n");
            innerException = innerException.getCause();
        }

        return sb.toString();
    }
}
