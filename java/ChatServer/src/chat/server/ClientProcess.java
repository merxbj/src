package chat.server;

import java.net.*;
import java.io.*;
import chat.common.*;

public class ClientProcess implements Runnable{

    public void run() {
        try {
            this.is = clientSocket.getInputStream();
            this.os = clientSocket.getOutputStream();

            System.out.println("Established connection!");

            DataBearer db = new DataBearer("Hello connected world");
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(db);

            System.out.println("Closing connection!");
            clientSocket.close();
        } catch (IOException e) {
            ExceptionHandler.HandleException(e);
        }
    }

    public ClientProcess(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private final Socket clientSocket;
    private InputStream is;
    private OutputStream os;

}
