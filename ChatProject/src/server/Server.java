package server;

import socketChat.SharedLibrary;

/**
 * Created by Kale on 11/15/2015.
 */
public class Server {
    public static void main(String[] args) {
        System.out.println("I'm a server!");
        Session[] sessions = new Session[SharedLibrary.MAX_SESSIONS];
        sessions[0] = new Session("localhost",55601); //default session
        ConnectionsManager cm = new ConnectionsManager();
        try {
            int conns = 0;
            while(conns < SharedLibrary.MAX_CONNECTIONS) {
                cm.addConnection(SharedLibrary.DEFAULT_PORT + 100 + conns);
                System.out.printf("Connection %d is connected.", conns + 1);
                conns++;
            }
        } catch(Exception e) {
            System.out.println(e);
        }

        try {
            Thread.sleep(300);
        } catch(Exception e) {
            System.out.println("interrupt");
        }
        cm.closeAll();
    }
}
