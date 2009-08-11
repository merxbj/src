package game.client;

import java.net.*;
import game.common.*;

public class ConnectionManager {

    public ConnectionManager(ClientShell shell) {
        this.shell = shell;
        sock = new Socket();
    }

    public boolean connect(String address, int port) {
        try {
            InetAddress addr = InetAddress.getByName(address);
            InetSocketAddress sockAddr= new InetSocketAddress(addr, port);
            return connect(sockAddr, shell.maxConnectionRetries);
        } catch (Exception ex) {
            shell.handleException(ex);
            return false;
        }
    }
    
    private boolean connect(InetSocketAddress server, int maxAttempts) {

        boolean success = false;
        int retries = maxAttempts;

        shell.log(String.format("Establishing connection to %s", server.toString()));
        shell.log("Please wait ...");

        while (!success && retries-- > 0) {
            try {
                
                shell.log(String.format("Attempt %d of %d", maxAttempts - retries, maxAttempts));

                sock.connect(server);
                success = true;

                shell.log("Connected to host ...");

            } catch (Exception ex) {
                if (retries == 0)
                    shell.handleException(ex);
            }
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
    private ClientShell shell;
}
