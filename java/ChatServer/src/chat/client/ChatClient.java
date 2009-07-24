package chat.client;

import java.net.*;
import java.io.*;
import chat.common.*;

public class ChatClient {
    
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("localhost", 22222); // hned se připojíme

            /*BufferedReader br = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));*/

            InputStream is = sock.getInputStream();
            OutputStream os = sock.getOutputStream();

            ObjectInputStream ois = new ObjectInputStream(is);
            
            DataBearer db = new DataBearer("");
            db = (DataBearer) ois.readObject();

            System.out.println(db.data);

            sock.close(); // zavření socketu

        } catch (Exception e) {
            ;
        }

    }
}
