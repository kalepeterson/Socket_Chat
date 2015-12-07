package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import client.Connection;
import client.Conversation;
import server.Database;
import client.ChatCommand;

/**
 * Created by Kale on 12/5/2015.
 */
public class ClientListener implements Runnable {
    private Socket sock;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Database db;
    private boolean connected;

    public ClientListener(Socket s, ObjectInputStream i, ObjectOutputStream o, Database d) {
        sock = s;
        in = i;
        out = o;
        connected = true;
        db = d;
    }

    public void run() {
        while(connected) {
            try {
                ChatCommand cM = waitForResponse();
                processMessage(cM);
            } catch(Exception e ) {
                System.out.println("ERROR: An exception occurred:");
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
            System.out.println("ERROR: Socket failed to close:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public ChatCommand waitForResponse() throws Exception {
        return ((ChatCommand) (in.readObject()));
    }

    public boolean processMessage(ChatCommand cM) throws Exception {
        switch(cM.cmdType) {
            case READMESSAGES:
                System.out.println("READ: " + cM);
                List<Conversation> convs = db.getConversation(cM.msg.getSender());
                for(int i = 0; i < convs.size(); i++) {
                    System.out.println("Sending conversation " + i);
                    out.writeObject(convs.get(i));
                }
                out.writeObject(null);
                break;
            case SENDMESSAGE:
                System.out.println("SEND: " + cM);
                db.addMessage(cM.msg);
                break;
            case INCORRECT:
                break;
            case PING:
                break;
            default:
                throw(new Exception("ERROR: Invalid client input command."));
        }
        return false;
    }
}
