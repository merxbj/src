package chat.server;

import java.net.*;
import chat.common.*;

public class ChatServer {

    public static void main(String[] args) {

        boolean quit = false;

        try {
            ServerSocket ss = new ServerSocket(22222);

            while (!quit) {
                final Socket sock = ss.accept();
                Thread t = new Thread(new ClientProcess(sock));
                t.setDaemon(true);
                t.start();
            }

            ss.close();
        } catch (Exception e) {
            ExceptionHandler.HandleException(e);
        }
    }
}
