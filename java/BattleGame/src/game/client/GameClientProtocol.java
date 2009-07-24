package game.client;

public class GameClientProtocol {

    public GameClientProtocol(GameShell shell) {
        conMan = new ConnectionManager(shell);
    }

    public void processCommand(String command) {
        if (!conMan.isConnectionEstablished()) {
            establishConnection();
        }

        switch (command) {
            case "connect
        }
    }

    private boolean establishConnection() {

    }

    private ConnectionManager conMan;
}
