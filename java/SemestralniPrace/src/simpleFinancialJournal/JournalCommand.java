package simpleFinancialJournal;

import java.util.*;

/**
 *
 * @author eTeR
 */
public class JournalCommand {

    public JournalCommand() {
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


    /*
     * I'm going to involve a nasty hack here, as if i want to allow user to create
     * description for entry with spaces (such an unusual thing!), I would have to
     * employ some other interface, how to collect parameters.
     * However, I want utilize the shell and command now, therefore I will consider
     * first parameter as an amount and consolidate all remaining raw parameters to 
     * one standalone string containing "spaced" description
     */
    private boolean buildAddParams() {
        if (rawParameters.size() > 1) {
            try {
                Double rawAmount = Double.parseDouble(rawParameters.get(0));
                Money amount = new Money(Math.round(Math.ceil(rawAmount * 100)));
                parameters.add(amount);
            } catch (NumberFormatException nfe) {
                return false;
            }

            Iterator it = rawParameters.iterator();
            String description = new String("");
            it.next(); // let's skip the first raw parameter (we have alread handled it before)
            while (it.hasNext()) {
                String partialDesc = (String) it.next();
                description = description + partialDesc + " ";
            }
            parameters.add(description);

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
        if (rawParameters.size() == 1) {
            if ((rawParameters.get(0).compareTo("journals") == 0)
                    || (rawParameters.get(0).compareTo("entries") == 0)) {
                parameters.add(rawParameters.get(0));
                return true;
            }
        }

        return false;
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
        LIST, BALANCE, SORT, ADD, REMOVE, CREATE,
        QUIT
    }
}
