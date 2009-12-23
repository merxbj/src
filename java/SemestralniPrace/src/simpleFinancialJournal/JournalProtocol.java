/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleFinancialJournal;

import java.util.*;
import java.io.*;

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
        clearJournals();
    }

    public void processCommand(JournalCommand command) throws Exception{

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
        Iterator iter = journals.iterator();

        while (iter.hasNext()) {
            Journal j = (Journal) iter.next();
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
        if (selectedJournal == null) {
            shell.log("You must select journal before you want to create any entry!");
            return;
        }

        JournalEntry je = new JournalEntry();
        je.setAmount((Money) command.parameters.get(0));
        je.setDescription((String) command.parameters.get(1));

        selectedJournal.add(je);
    }

    private void showData(JournalCommand command) {
        if (((String) command.parameters.get(0)).compareTo("entries") == 0) {
            listEntries();
        } else {
            listJournals();
        }
    }

    private void listEntries() {
        if (selectedJournal == null) {
            shell.log("You must select journal before you want to list any!");
            return;
        }

        Iterator it = selectedJournal.iterator();
        while (it.hasNext()) {
            JournalEntry je = (JournalEntry) it.next();
            shell.log(je.toString());
        }
    }

    private void listJournals() {
        Iterator it = journals.iterator();
        while (it.hasNext()) {
            Journal j = (Journal) it.next();
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
    }

    private IdentifierGenerator idGenerator;
    private TreeSet<Journal> journals;
    private Journal selectedJournal;
    private Shell shell;
    private File assignedFile;
}
