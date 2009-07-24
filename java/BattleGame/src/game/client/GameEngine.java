package game.client;

public class GameEngine {

    public GameEngine() {
        shell = new GameShell();
        prot = new GameClientProtocol(shell);
    }

    public void run() {

        String input = "";
        
        while (shell.requestInput(input)) {
            prot.processCommand(input);
        }

    }

    private GameShell shell;
    private GameClientProtocol prot;
}
