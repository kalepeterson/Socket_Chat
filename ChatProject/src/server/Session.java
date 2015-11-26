package server;

import java.util.LinkedList;
import socketChat.*;

/**
 * Created by Kale on 11/15/2015.
 */
public class Session {

    private ConnectionsManager conMan;
    private int startPort; //first valid port in this session's range
    private int endPort; //last valid port in this session's range
    private LinkedList<Integer> lostConnections; //contains port numbers of clients that disconnected
    private String hostName;
    private String password;
    private User[] users;

    /**
     * Constructor for a publicly available session, the password will be the default.
     * @param host The hostname that this session will listen on
     * @param startPort The first valid port for this session
     */
    public Session(String host, int startPort) {
        this.hostName = host;
        this.password = SharedLibrary.DEFAULT_PASSWORD;
        this.startPort = startPort;
        this.endPort = startPort + SharedLibrary.MAX_CONNECTIONS;
        /*int[] temp = new int[SharedLibrary.MAX_CONNECTIONS];
        int i = 0;
        for(int port = startPort; port <= endPort; port++) {
            temp[i++] = port;
        }*/
        users = new User[SharedLibrary.MAX_CONNECTIONS];
        conMan = new ConnectionsManager();
        //threading stuff
    }

    /**
     * Constructor for a private session with a custom password.
     * @param host The hostname that this session will listen on
     * @param startPort The first valid port for this session
     * @param pw The custom password for this session
     */
    public Session(String host, int startPort, String pw) {
        this.hostName = host;
        this.password = pw;
        this.startPort = startPort;
        this.endPort = startPort + SharedLibrary.MAX_CONNECTIONS;
        int[] temp = new int[SharedLibrary.MAX_CONNECTIONS];
        int i = 0;
        for(int port = startPort; port <= endPort; port++) {
            temp[i++] = port;
        }
        conMan = new ConnectionsManager();
        conMan.setPW(this.password);
        //threading stuff
    }
    /**
     * Gets the host name used by the connections.
     * @return The host name being used by the connections.
     */
    public String getHost() {
        return hostName;
    }
    public int getStartPort() {
        return startPort;
    }
    public int getEndPort() {
        return endPort;
    }
}
