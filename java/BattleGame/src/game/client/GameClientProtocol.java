package game.client;

public class GameClientProtocol {

    public GameClientProtocol(ClientShell shell) {
        this.shell = shell;
        conMan = new ConnectionManager(shell);
    }

    public void processCommand(GameCommand command) {

        switch (command.type) {
            case CONNECT:
                establishConnection(command);
                break;
            case DISCONNECT:
                closeConnection();
                break;
        }
    }

    private void establishConnection(GameCommand command) {
        if (!conMan.isConnectionEstablished()) {
            String addr = (String) command.parameters.get(0);
            int port = 22334; // this is temporarly choosen default port
            if (command.paramCount > 1)
                port = Integer.parseInt((String) command.parameters.get(1));

            conMan.connect(addr, port);
        } else {
            shell.log("Already connected to the server. Please, disconnet first.");
        }
    }

    private void closeConnection() {
        if (conMan.isConnectionEstablished()) {
            conMan.disconnect();
        } else {
            shell.log("You are not connected!");
        }
    }

    private ConnectionManager conMan;
    private ClientShell shell;
}
