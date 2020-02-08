/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplefiletransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author merxbj
 */
public class SimpleFileTransfer {

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            boolean quit = false;
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));

            while (!quit) {
                final Socket sock = ss.accept();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("Client connected!");
                        OutputStream os = null;
                        FileInputStream fis = null;
                        try {
                            os = sock.getOutputStream();
                            File f = new File(args[1]);
                            fis = new FileInputStream(f);
                            byte[] buffer = new byte[65536];
                            int len = fis.read(buffer);
                            long trans = len;
                            while (len > 0) {
                                os.write(buffer, 0, len);
                                System.out.printf("Transfered %6.3fMB / %6.3fMB\n", (trans / 1024.0 / 1024.0), (f.length() / 1024.0 / 1024.0));
                                len = fis.read(buffer);
                                trans += len;
                            }
                            System.out.println("Successfully transfered!");
                            os.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } 
                        finally {
                            try {
                                fis.close();
                                os.close();
                            } catch (Exception ex) {}
                        }
                    }
                }).start();
            }

            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
