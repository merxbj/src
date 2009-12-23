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
public class PersistentJournal {

    public PersistentJournal() {
        persistentEntries = new TreeSet<PersistentJournalEntry>();
    }

    /*
     * Deserializes all entries from the binary files. Meanwhile it suspends
     * id generator on the Journal object. This will prevent the id's to be
     * recalculated during the add phase.
     * As soon as the journal is complete, the generator is kicked off again.
     */
    public void deserialize(File file, Set<Journal> journals) throws Exception {

        persistentEntries.clear();
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            while (true) {
                PersistentJournalEntry pje = pje = (PersistentJournalEntry) ois.readObject();
                if (pje != null) {
                    persistentEntries.add(pje);
                }
            }
        } catch (EOFException ex) {
            // we have just reached the end of file
        }
        finally {
            if (ois != null)
                ois.close();
        }

        Iterator it = persistentEntries.iterator();
        Journal journal = null;

        while (it.hasNext()) {

            PersistentJournalEntry pje = (PersistentJournalEntry) it.next();
            
            JournalEntry je = new JournalEntry();
            je.setEntryId(pje.getEntryId());
            je.setAmount(pje.getAmount());
            je.setDescription(pje.getDescription());

            Integer journalId = pje.getJournalId();
            if (journal == null) {
                journal = new Journal(journalId);
                journal.suspendGenerator();
            }
            else if (journal.getJournalId() != journalId) {
                journals.add(journal);
                journal.resumeGenerator();
                journal = new Journal(journalId);
            }

            journal.add(je);
        }
        journals.add(journal);
        journal.resumeGenerator();
    }

    public void serialize(File file, Set<Journal> journals) throws Exception {
        
        persistentEntries.clear();
        Iterator jit = journals.iterator();

        while (jit.hasNext()) {
            Journal j = (Journal) jit.next();
            int journalId = j.getJournalId();

            Iterator jeit = j.iterator();
            while (jeit.hasNext()) {
                JournalEntry je = (JournalEntry)jeit.next();

                PersistentJournalEntry pje = new PersistentJournalEntry();
                pje.setAmount(je.getAmount());
                pje.setDescription(je.getDescription());
                pje.setEntryId(je.getEntryId());
                pje.setJournalId(journalId);

                persistentEntries.add(pje);
            }
        }

        Exception unexpectedException = null;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            Iterator peit = persistentEntries.iterator();
            while (peit.hasNext()) {
                PersistentJournalEntry pje = (PersistentJournalEntry) peit.next();
                oos.writeObject(pje);
            }
        } catch (Exception ex) {
            unexpectedException = ex;
        } finally {
            oos.close();
        }

        /*
         * This might look quite weird, but as the exception could occure during
         * the journal save, we don't want only to swallow it, but besides acting
         * properly by closing the stream, rethrow it as well.
         * 
         */
        if (unexpectedException != null) {
            throw new Exception("Unexpected exception thrown during journal save!", unexpectedException);
        }
    }

    private TreeSet<PersistentJournalEntry> persistentEntries;
}
