package simpleFinancialJournal;

/**
 *
 * @author eTeR
 */
public class SimpleFinancialJournal {

    public SimpleFinancialJournal() {
        shell = new Shell();
        prot = new JournalProtocol(shell);
    }

    public void Run() {

        JournalCommand command = new JournalCommand();

        while (shell.requestCommand(command)) {
            try {
                prot.processCommand(command);
            } catch (Exception ex) {
                shell.handleException(ex);
            } finally {
                command.clear();
            }

        }

    }
    private Shell shell;
    private JournalProtocol prot;
}
