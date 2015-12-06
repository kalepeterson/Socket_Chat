package server;

import socketChat.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Kale on 12/5/2015.
 */
public class ClientListener implements Runnable {
    private Socket sock;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean connected;

    public ClientListener(Socket s, ObjectInputStream i, ObjectOutputStream o) {
        sock = s;
        in = i;
        out = o;
        connected = true;
    }

    public void run() {
        while(connected) {
            try {
                Message m = waitForResponse();
                processMessage(m);
            } catch(Exception e ) {
                System.out.println("An exception occurred:");
                System.out.println(e.getMessage());
                e.printStackTrace();
                connected = false;
            }
        }
        try {
            sock.close();
            in = null;
            out = null;
        } catch(Exception e) {
            System.out.println("Socket failed to close:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Message waitForResponse() throws Exception {
        return ((Message) (in.readObject()));
    }

    public void processMessage(Message m) throws Exception {
        System.out.println(m);
    }
}
