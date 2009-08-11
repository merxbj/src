package game.client;

public class GameEngine {

    public GameEngine() {
        shell = new ClientShell();
        prot = new GameClientProtocol(shell);
    }

    public void run() {

        GameCommand command = new GameCommand();
        
        while (shell.requestCommand(command)) {
            prot.processCommand(command);
            command.clear();
        }

    }

    private ClientShell shell;
    private GameClientProtocol prot;
}
