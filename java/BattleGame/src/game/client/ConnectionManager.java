package game.client;

import java.net.*;
import game.common.*;

public class ConnectionManager {

    public ConnectionManager(GameShell shell) {
        this.shell = shell;
        sock = new Socket();
    }

    public boolean connect(InetSocketAddress server) {

        boolean success = true;

        try {
            shell.log(String.format("Establishing connection to %s", server.toString()));
            shell.log("Please wait ...");

            sock.connect(server);

            shell.log("Connected to host ...");

        } catch (Exception ex) {
            shell.handleException(ex);
            success = false;
        }

        return success;
    }

    public boolean disconnect() {

        boolean success = true;

        try {
            shell.log("Disconnecting from host ...");

            sock.close();

            shell.log("Disconnected! Bye ...");
        } catch (Exception ex) {
            shell.handleException(ex);
            success = false;
        }

        return success;
    }

    public boolean send(DataBearer db) {
        return true;
    }

    public boolean isConnectionEstablished() {
        return sock.isConnected();
    }

    private Socket sock;
    private GameShell shell;
}
