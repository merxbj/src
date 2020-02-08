package simpleFinancialJournal;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 *
 * @author eTeR
 */
public class JournalProtocol {

    public JournalProtocol(Shell shell) {
        this.shell = shell;
        this.journals = new TreeSet<Journal>();
        this.idGenerator = IdentifierGenerator.getInstance();
        idGenerator.initJournalIdGenerator(journals);
        this.assignedFile = null;
        hasUnsavedChanges = false;
        clearJournals();
    }

    public void processCommand(JournalCommand command) throws Exception {

        switch (command.type) {
            case ASSIGN:
                assignFile(command);
                break;
            case HELP:
                provideHelp();
                break;
            case CREATE:
                createNewJournal();
                break;
            case SELECT:
                selectActiveJournal(command);
                break;
            case ADD:
                addJournalEntry(command);
                break;
            case LIST:
                showData(command);
                break;
            case SAVE:
                saveData();
                break;
            case CLOSE:
                closeFile(command);
                break;
            case SORT:
                sortJournal(command);
                break;
            case REMOVE:
                removeJournalEntry(command);
                break;
            case BALANCE:
                printBalance();
                break;
            case UPDATE:
                updateRecord(command);
                break;
        }
    }

    /*
     * Assign a persistent journal file, loads its content to the journal set
     * (if exists), otherwise establishes a new empty one
     */
    private void assignFile(JournalCommand command) throws Exception {
        String filePath = (String) command.parameters.get(0);
        File f = new File(filePath);

        if (f.exists()) {
            clearJournals();
            PersistentJournal pj = new PersistentJournal();
            pj.deserialize(f, journals);
        } else {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }

        idGenerator.initJournalIdGenerator(journals);
        assignedFile = f;
        shell.log(String.format("File %s assigned...", f.getAbsolutePath()));
    }

    private void provideHelp() {
        shell.printHelp();
    }

    private void createNewJournal() {
        Journal j = new Journal(idGenerator.getNextJournalId());
        journals.add(j);
        hasUnsavedChanges = true;
        shell.log(String.format("New journal with id %d created", j.getJournalId()));
    }

    private void clearJournals() {
        for (Journal j : journals) {
            j.clear();
        }

        journals.clear();
    }

    /*
     * Yes, this search is quite ineffective, but anyway there will not be
     * many journals, yet ...
     */
    private void selectActiveJournal(JournalCommand command) {
        int journalId = (Integer) command.parameters.get(0);

        for (Journal j : journals) {
            if (j.getJournalId() == journalId) {
                selectedJournal = j;
                shell.log(String.format("Selected journal with id %d", journalId));
                return;
            }
        }

        shell.log(String.format("Unable to find journal with id %d", journalId));

    }

    /*
     * Add new entry to the Journal.
     * Journal will get the journalEntryId by itself.
     */
    private void addJournalEntry(JournalCommand command) {
        if (checkSelectedJournal()) {
            JournalEntry je = new JournalEntry();
            je.amount = (Money) command.parameters.get(0);
            je.description = (String) command.parameters.get(1);
            selectedJournal.add(je);
            hasUnsavedChanges = true;
        }
    }

    private void showData(JournalCommand command) throws JournalException {
        if (((String) command.parameters.get(0)).compareTo("entries") == 0) {
            listEntries(command);
        } else {
            listJournals();
        }
    }

    private void listEntries(JournalCommand command) throws JournalException {
        if (checkSelectedJournal()) {
            for (JournalEntry je : selectedJournal) {
                if (!command.parameters.contains("*")) {
                try {
                    Class<?> c = je.getClass();
                    Field restrictedField = c.getDeclaredField((String) command.parameters.get(1));
                    Object value = restrictedField.get(je);

                    if (((String) command.parameters.get(2)).compareTo("is") == 0) {
                        if (!value.equals(command.parameters.get(3)))
                            continue;
                    } else if (value instanceof String) {
                        if (!((String) value).contains((String) command.parameters.get(3)))
                            continue;
                    }
                } catch (Exception ex) {
                    throw new JournalException("Invalid where clause!");
                }
            }
                shell.log(je.toString());
            }
        }
    }

    private void listJournals() {
        for (Journal j : journals) {
            shell.log(j.toString());
        }
    }

    private void saveData() throws Exception {
        if (assignedFile == null) {
            shell.log("You must assign a file before you can save anything!");
            return;
        }

        PersistentJournal pj = new PersistentJournal();
        pj.serialize(assignedFile, journals);
        hasUnsavedChanges = false;
    }

    private void removeJournalEntry(JournalCommand command) {
        if (checkSelectedJournal()) {
            int journalId = (Integer) command.parameters.get(0);
            for (JournalEntry je : selectedJournal) {
                if (je.entryId == journalId) {
                    selectedJournal.remove(je);
                    hasUnsavedChanges = true;
                    return;
                }
            }
            shell.log(String.format("Unable to find journal entry with id %d", journalId));
        }
    }

    private boolean checkSelectedJournal() {
        if (selectedJournal == null) {
            shell.log("You must select journal before you want to list any!");
            return false;
        }
        return true;
    }

    private void closeFile(JournalCommand command) {
        if (assignedFile != null) {
            if (hasUnsavedChanges && !command.parameters.contains("forced")) {
                shell.log("Journal contains unsaved changes!");
                shell.log("If you want to close it anyway, please use close forced!");
                return;
            }
            clearJournals();
            hasUnsavedChanges = false;
            selectedJournal = null;
            assignedFile = null;
        }
    }

    private void sortJournal(JournalCommand command) {
        if (checkSelectedJournal()) {
            SortColumn column = new SortColumn((String) command.parameters.get(0));
            SortOrder order = new SortOrder((String) command.parameters.get(1));

            if (column.getColumn().equals(SortColumn.Column.UNSUPPORTED)) {
                shell.log(String.format("Unsupported sort column \"%s\"!", column));
                return;
            } else if (order.getOrder().equals(SortOrder.Order.UNKNOWN)) {
                shell.log(String.format("Unknown sort order \"%s\" provided!", column));
                return;
            }

            selectedJournal.setSortColumn(column);
            selectedJournal.setSortOrder(order);

            selectedJournal.sort();
        }
    }

    private void printBalance() {
        if (checkSelectedJournal()) {
            Money journalBalance = new Money(0);
            for (JournalEntry je : selectedJournal) {
                journalBalance.setValue(journalBalance.getValue() + je.amount.getValue());
            }
            shell.log(String.format("Journal %d balance: %s", selectedJournal.getJournalId(), journalBalance));
        }
    }

    private void updateRecord(JournalCommand command) throws Exception {
        if (checkSelectedJournal()) {
            JournalEntry je = new JournalEntry();
            je.entryId = (Integer) command.parameters.get(0);
            je.amount = (Money) command.parameters.get(1);
            je.description = (String) command.parameters.get(2);

            selectedJournal.update(je);
            hasUnsavedChanges = true;
        }
    }

    private IdentifierGenerator idGenerator;
    private TreeSet<Journal> journals;
    private Journal selectedJournal;
    private Shell shell;
    private File assignedFile;
    private boolean hasUnsavedChanges;
}
