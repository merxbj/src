package simpleFinancialJournal;

import java.util.*;

/**
 * Class encapsulating a single user command with its parameters. It provides
 * a method for parse a single command line input.
 */
public class JournalCommand {

    public JournalCommand() {
        rawName = "";
        rawParameters = new ArrayList<String>();
        parameters = new ArrayList<Object>();
        paramCount = 0;
        type = CommandType.NOTHING;
    }

    /*
     * Parses a single command line input, validates it and initializes the 
     * command itself into the source of flow.
     */
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

    /*
     * The command type is derived from the first word parsed from user input
     */
    private void assignCommandType() {

        if (rawName.compareTo("assign") == 0) {
            type = CommandType.ASSIGN;
        } else if (rawName.compareTo("save") == 0) {
            type = CommandType.SAVE;
        } else if (rawName.compareTo("close") == 0) {
            type = CommandType.CLOSE;
        } else if ((rawName.compareTo("quit") == 0) || (rawName.compareTo("exit") == 0)) {
            type = CommandType.QUIT;
        } else if (rawName.compareTo("add") == 0) {
            type = CommandType.ADD;
        } else if (rawName.compareTo("remove") == 0) {
            type = CommandType.REMOVE;
        } else if (rawName.compareTo("help") == 0) {
            type = CommandType.HELP;
        } else if (rawName.compareTo("select") == 0) {
            type = CommandType.SELECT;
        } else if (rawName.compareTo("create") == 0) {
            type = CommandType.CREATE;
        } else if (rawName.compareTo("balance") == 0) {
            type = CommandType.BALANCE;
        } else if (rawName.compareTo("sort") == 0) {
            type = CommandType.SORT;
        } else if (rawName.compareTo("list") == 0) {
            type = CommandType.LIST;
        } else if (rawName.compareTo("update") == 0) {
            type = CommandType.UPDATE;
        }
    }

    private boolean buildAssignParams() {
        if (rawParameters.size() == 1) {
            parameters.add(rawParameters.get(0));
        } else {
            parameters.add(".\\SimpleJournal.dat"); // default file name
        }

        paramCount = parameters.size();
        return true;
    }

    /*
     * We are creating only journals now, therefore now logic is involved here.
     */
    private boolean buildCreateParams() {
        return true;
    }

    /*
     * Select params are only the journal id which we want to select
     */
    private boolean buildSelectParams() {
        Integer journalId;

        try {
            journalId = Integer.parseInt(rawParameters.get(0));
        } catch (Exception ex) {
            return false;
        }

        parameters.add(journalId);

        return true;
    }


    private boolean buildAddParams() {
        if (rawParameters.size() > 1) {
            try {
                Money amount = new Money(Double.parseDouble(rawParameters.get(0)));
                parameters.add(amount);
                parameters.add(buildString(1));
            } catch (Exception ex) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    /*
     * Just verify that we have got correct parameters. Let the consumer decide
     * how to deal with it.
     */
    private boolean buildListParams() {
        if (rawParameters.size() > 0) {
            if (rawParameters.get(0).compareTo("journals") == 0) {
                parameters.add(rawParameters.get(0));
                return true;
            } else if (rawParameters.get(0).compareTo("entries") == 0) {
                parameters.add(rawParameters.get(0));
                return buildWhereClause();
            }
        }

        return false;
    }

    /*
     * There I'm very stupidly trying to parse restricting value as a double
     * When this not succeed, I will try to parse it as a string
     */
    private boolean buildWhereClause() {
        if ((rawParameters.size() > 1) && (rawParameters.get(1).compareTo("where") == 0)) {
            try {
                parameters.add(rawParameters.get(2)); // column name
                if ((rawParameters.get(3).compareTo("is") == 0) ||
                        (rawParameters.get(3).compareTo("contains") == 0)) {
                    parameters.add(rawParameters.get(3));
                }
                try {
                    double dValue;
                    dValue = Double.parseDouble(rawParameters.get(4));
                    parameters.add(new Money(dValue));
                } catch (Exception ex) {
                    parameters.add(buildString(4));
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        } else {
            parameters.add("*");
            return true;
        }
    }

    private boolean buildRemoveParams() {
        if (rawParameters.size() == 1) {
            try {
                int journalEntryId = Integer.parseInt(rawParameters.get(0));
                parameters.add(journalEntryId);
                return true;
            } catch (Exception ex) {
            }
        }
        return false;
    }


    /*
     * The sense of the forced param is to allow user to close the journal even
     * when there are unsaved changes in it.
     */
    private boolean buildCloseParams() {
        if (rawParameters.size() == 1) {
            if (rawParameters.get(0).compareTo("forced") == 0) {
                parameters.add(rawParameters.get(0));
            } else {
                return false;
            }
        }
        return true;
    }

    /*
     * The command would look like "sort by *** desc/asc" to be more user readable.
     * However, we will omit the "by" word and add only the column to the params
     * If asc/desc is not found, asc is used as default
     */
    private boolean buildSortParams() {
        if (rawParameters.size() == 2) {
            parameters.add(rawParameters.get(1));
            parameters.add(new String("asc"));
            return true;
        } else if (rawParameters.size() == 3) {
            if ((rawParameters.get(2).compareTo("asc") == 0) ||
                (rawParameters.get(2).compareTo("desc") == 0)) {
                parameters.add(rawParameters.get(1));
                parameters.add(rawParameters.get(2));
                return true;
            }
        }

        return false;
    }

    /*
     * Update command takes only record id as the argument on the top of the add
     * command. Therefore the record id will be parsed out, removed from raw
     * paremeters and then buildAddParams will be called to take care about the
     * rest.
     */
    private boolean buildUpdateParams() {
        if (rawParameters.size() > 0) {
            try {
                parameters.add(Integer.parseInt(rawParameters.get(0)));
                rawParameters.remove(0);
                return buildAddParams();
            } catch (Exception ex) {
                
            }
        }

        return false;
    }

    /*
     * I'm going to involve a nasty hack here, as if i want to allow user to create
     * description for entry with spaces (such an unusual thing!), I would have to
     * employ some other interface, how to collect parameters.
     * However, I want utilize the shell and command now, therefore I will consider
     * first parameter as an amount and consolidate all remaining raw parameters to 
     * one standalone string containing "spaced" description
     */
    private String buildString(int firstToken) throws JournalException {
        String builtString = new String("");
        int i = firstToken;
        boolean valid = true;

        String partialString = rawParameters.get(i++);
        if (partialString.indexOf("'") != 0)
            throw new JournalException("Invalid string format!");

        builtString = builtString + partialString;
        
        while (i < rawParameters.size()) {
            valid = false;
            partialString = rawParameters.get(i);
             int stringTerminatorPos = partialString.indexOf("'");
             if ((stringTerminatorPos != -1) && (stringTerminatorPos != (partialString.length() - 1))) {
                 throw new JournalException("Invalid string format!");
             } else if (stringTerminatorPos != -1) {
                 builtString = builtString + " " + partialString;
                 valid = true;
                 break; // this should be the end of the string
             } else {
                builtString = builtString + " " + partialString;
             }
             i++;
        }

        if (!valid) {
            throw new JournalException("Invalid string format!");
        } else {
            // don't forget to cut off the leading and trailing quotas!
            builtString = builtString.substring(1, builtString.length() - 1);
        }

        return builtString;
    }


    /*
     * Every single command may or may not use parameters as an additional source
     * of information. This function is called everytime the valid command
     * is recognized and decides, whether the additional parameters may have any
     * meaning, validates them and builds a list of parameters upon them (when
     * they are valid)
     */
    private boolean buildCommand() {
        boolean success;

        switch (type) {
            case ASSIGN:
                success = buildAssignParams();
                break;
            case CREATE:
                success = buildCreateParams();
                break;
            case SELECT:
                success = buildSelectParams();
                break;
            case ADD:
                success = buildAddParams();
                break;
            case LIST:
                success = buildListParams();
                break;
            case REMOVE:
                success = buildRemoveParams();
                break;
            case CLOSE:
                success = buildCloseParams();
                break;
            case SORT:
                success = buildSortParams();
                break;
            case UPDATE:
                success = buildUpdateParams();
                break;
            case NOTHING:
                success = false;
                break;
            default:
                success = true;
                break;
        }

        if (!success) {
            type = CommandType.NOTHING;
        }

        return success;
    }
    private String rawName;
    private ArrayList<String> rawParameters;
    public int paramCount;
    public ArrayList<Object> parameters;
    public CommandType type;

    public enum CommandType {

        NOTHING, HELP,
        ASSIGN, CLOSE, SAVE, SELECT,
        LIST, BALANCE, SORT, ADD, REMOVE, CREATE, UPDATE,
        QUIT
    }
}
