package server;

import socketChat.SharedLibrary;

/**
 * Created by Kale on 11/15/2015.
 */
public class Server {

    public static void main(String[] args) {
        System.out.println("Server start.");
        Database db = new Database();
        ConnectionsManager cm = new ConnectionsManager();
        int conns = 0;
        while(conns < SharedLibrary.MAX_CONNECTIONS) {
            try {
                cm.addConnection(SharedLibrary.DEFAULT_PORT + 100 + conns);
                System.out.printf("Connection %d is connected.", conns + 1);
                conns++;
            } catch(Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        System.out.println("Maximum connections reached.");
        try {
            Thread.sleep(300);
        } catch(Exception e) {
            System.out.println("Interrupt");
        }
        cm.closeAll();
    }
}
