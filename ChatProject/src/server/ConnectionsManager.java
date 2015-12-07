package server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

import client.ChatCommand;
import client.Conversation;
import server.Database;
import server.DatabaseElement;
import socketChat.Message;
import socketChat.SharedLibrary;
import socketChat.User;

/**
 * This class is designed to make interacting with the four sockets easier.
 * @author Kale
 */
public class ConnectionsManager {

    //constants
    private final int MAX_CONNS = SharedLibrary.MAX_CONNECTIONS; //max number of connections per session
    private final int DEFAULT_PORT = SharedLibrary.DEFAULT_PORT; //port number to initially connect to
    private String PW = SharedLibrary.DEFAULT_PASSWORD;
    //private arrays
    private Socket[] socks; //the actual socket connections
    private ObjectInputStream[] sockIns; //for reading from a specified socket
    private ObjectOutputStream[] sockOuts; //for sending information to a specified socket
    private ClientListener[] cl;
    public final Database db;
    
    
    //private fields
    private byte numConnections; //The number of current connections

    //constructors
    /**
     * Default constructor, used if connections are expected to be added to ports determined after object creation.
     */
    public ConnectionsManager() {
        //init all player connections, plus one for default ([0])
        socks = new Socket[MAX_CONNS+1];
        sockIns = new ObjectInputStream[MAX_CONNS+1];
        sockOuts = new ObjectOutputStream[MAX_CONNS+1];
        cl = new ClientListener[MAX_CONNS+1];
        numConnections = 0;
        this.db = new Database();
    }
                           //getters
                           /**
     * Gets the number of connected clients.
     * @return The number of connections.
     */
                           public byte getNumConnections() {
        return numConnections;
    }

    //setters and add methods
    /**
     * Sets the password for client connections.
     * @param customPassword The password that clients must send to connect.
     */
    public void setPW(String customPassword) {
        PW = customPassword;
    }
    /**
     * Adds a connection with the current host name.
     * @param port The port number to connect to.
     * @throws Exception Thrown if the number of connections is already at maximum or if the connection fails.
     * @return The response from the client when connection to port is established.
     */
    public User addConnection(int port) throws Exception {
        if (numConnections < MAX_CONNS) {
            try {
                //First, the server is listening on the default port.
                ServerSocket ss = new ServerSocket(DEFAULT_PORT);
                System.out.printf("Waiting for connection #%d on port %d\n", numConnections + 1, port);
                socks[0] = ss.accept();
                System.out.println("Accepted.");
                sockOuts[0] = new ObjectOutputStream(socks[0].getOutputStream());
                sockIns[0] = new ObjectInputStream(socks[0].getInputStream());
                //The client then responds with the password.
                System.out.println("Reading password.");
                String rsp = (String) sockIns[0].readObject(); //pw
                //Check the password
                if (rsp.equals(PW)) {
                    //Send the new port number to the client
                    System.out.println("Password correct! Sending new port");
                    sockOuts[0].writeInt(port);
                } else {
                    //Wrong password, send -1 so the client can be notified
                    System.out.println("PASSWORD INCORRECT");
                    sockOuts[0].writeInt(-1);
                    //Close the default port
                    closeDefaults();
                    ss.close();
                    //Let the server application know that this didn't work.
                    throw new Exception("Invalid password");
                }
                //Successful, close the default port for now.
                closeDefaults();
                ss.close();
                //Shift to the new port number and listen
                System.out.println("Waiting for connection on " + port);
                ss = new ServerSocket(port);
                socks[numConnections + 1] = ss.accept();
                System.out.println("Connection made!");
                //Connected, no need for the ServerSocket now.
                ss.close();
                //Make the input/output streams.
                sockOuts[numConnections + 1] = new ObjectOutputStream(socks[numConnections + 1].getOutputStream());
                sockIns[numConnections + 1] = new ObjectInputStream(socks[numConnections + 1].getInputStream());
                //The client will now send the User object created on their side.
                System.out.println("Reading user");
                User ursp = (User) sockIns[numConnections + 1].readObject();
                //Set the User's unique ID and send it back to the client.
                //NOTE: If we have time, make this more complicated.
                ursp.setUniqueID(numConnections + 1);
                //System.out.println("Sending new user id");
                //sockOuts[numConnections+1].writeInt(numConnections + 1);
                System.out.println("Sent.  Waiting for ready message");
                ChatCommand msg = (ChatCommand) waitForResponse(numConnections + 1);
                //Return the user to the server application
                cl[numConnections + 1] = new ClientListener(
                        socks[numConnections + 1],
                        sockIns[numConnections + 1],
                        sockOuts[numConnections + 1],
                        db
                );
                db.addUser(new DatabaseElement(ursp.getUserName()));
                Thread clientThread = new Thread(cl[numConnections + 1]);
                clientThread.start();
                System.out.printf("Socket #%d successfully connected.\nMessage: %s\n", ++numConnections, ursp);
                return ursp;
            } catch (Exception e) {
                System.out.println("An error occurred while opening socket.");
                System.out.println("---Socket attempt information---");
                System.out.printf("Socket #%d: host: %s\tport: %d\n", numConnections + 1, InetAddress.getByName(null), port);
                System.out.println(e.getMessage());
                throw new Exception(e);
            }
        } else {
            throw new Exception(String.format("%s %s  %s %d\n",
                    "ConnectionsManager.addConnection:",
                    "Attempted to add more connections than allowed.",
                    "Max connections:",
                    MAX_CONNS
            ));
        }
    }

    //actions
    /**
     * Removes the connection at the given index.
     * @param index The zero based index at which the connection will be removed.
     * @return True if removal was successful.
     * @throws ArrayIndexOutOfBoundsException Thrown if index is negative or exceeds the number of current connections.
     */
    public boolean removeConnection(int index) throws ArrayIndexOutOfBoundsException {
        if(index < numConnections && index > 0) { //0 not allowed because that's the default port
            if(socks[index] == null) {
                System.out.println("DEBUG: Something is wrong.  plyrConns[index] is null but index < numConnections.");
                return false;
            } else {
                try {
                    //sendMessage(CLOSE_MESSAGE,index);
                    sockOuts[index].close();
                    sockOuts[index] = null;
                    sockIns[index].close();
                    sockIns[index] = null;
                    socks[index].close();
                    socks[index] = null;
                    numConnections--;

                    //The following is commented out because it might actually be a bad idea.
                    //User IDs are most likely going to be associated with an index of socks, so moving them around
                    //might be unnecessary and annoying to deal with.

/*					shift connections left if needed
					if(index < numConnections) {
						for(int j = index+1; j < numConnections; j++) {
							if(socks[j] != null) {
								socks[index] = socks[j];
								sockOuts[index] = sockOuts[j];
								sockIns[index] = sockIns[j];
								socks[j] = null;
								sockOuts[j] = null;
								sockIns[j] = null;
								index++;
							}
						}
					}
*/
                    return true;
                } catch(Exception why) {
                    System.out.println("ConnectionsManager.removeConnection: " + why.getMessage());
                    return false;
                }
            }
        } else {
            throw new ArrayIndexOutOfBoundsException("ConnectionsManager.removeConnection: index " + index + " is invalid.");
        }
    }
    /**
     * Removes a connection at a given port number, if found.
     * @param port The port number to find.
     * @return True if the connections was successfully removed.
     */
    public boolean removeConnectionAtPort(int port) {
        for(int i = 1; i <= numConnections; i++) {
            if(socks[i].getPort() == port) {
                try {
                    //sendMessage(CLOSE_MESSAGE,i);
                    sockOuts[i].close();
                    sockOuts[i] = null;
                    sockIns[i].close();
                    sockIns[i] = null;
                    socks[i].close();
                    socks[i] = null;
                    numConnections--;

                    //The following is commented out because it might actually be a bad idea.
                    //Player numbers are most likely going to be associated with an index of socks, so moving them around
                    //might be unnecessary and annoying to deal with.

/*					shift connections left if needed
					if(index < numConnections) {
						for(int j = index+1; j < numConnections; j++) {
							if(socks[j] != null) {
								socks[index] = socks[j];
								sockOuts[index] = sockOuts[j];
								sockIns[index] = sockIns[j];
								socks[j] = null;
								sockOuts[j] = null;
								sockIns[j] = null;
								index++;
							}
						}
					}
*/
                    return true;
                } catch(Exception why) {
                    System.out.println("ConnectionsManager.removeConnection: " + why.getMessage());
                    return false;
                }
            }
        }
        return false;
    }
    /**
     * Writes a string to the socket at the given index.
     * @param message The string to write to the socket.
     * @param index The index of the connection to write to.
     * @throws Exception Thrown if the socket is not open for writing or if the index is invalid.
     */
    public void sendMessage(Object message, int index) throws Exception {
        if(index < numConnections+1 && index >= 0) {
            if(socks[index] != null) {
                sockOuts[index].writeObject(message);
            } else {
                throw new Exception("ConnectionsManager.sendMessage: Connection at index " + index + " is not writable.");
            }
        } else {
            throw new Exception("ConnectionsManager.sendMessage: index " + index + " is invalid.");
        }
    }
    /**
     * Sends message to all open connections.
     * @param message The message to be sent.
     */
    public void broadcast(Object message) {
        for(int i = 1; i <= numConnections; i++) {
            if(sockOuts[i] != null) {
                try {
                    sockOuts[i].writeObject(message);
                    //sockOuts[i].reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeAll() {
        for(ObjectOutputStream pw : sockOuts) {
            if(pw != null) {
                try {
                    pw.close();
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        for(ObjectInputStream br : sockIns) {
            if(br != null) {
                try {
                    br.close();
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        for(Socket s : socks) {
            if(s != null) {
                try {
                    s.close();
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    /**
     * Waits for a response from the connection at index.  Returns the message received.
     * @param index The index of the connection to listen to.
     * @return The received response from the specified connection.
     */
    public Object waitForResponse(int index) throws Exception {
        Object response;
        try {
            response = sockIns[index].readObject();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            //response = ERROR_MESSAGE;
            throw e;
        }
        return response;
    }

    //private methods
    /**
     * Closes the default Socket, ObjectInputStream, and ObjectOutputStream if they are not null.
     */
    private void closeDefaults() {
        try {
            if(!(sockOuts[0] == null)) {
                sockOuts[0].close();
            }
            if(!(sockIns[0] == null)) {
                sockIns[0].close();
            }
            if(!(socks[0] == null) && !socks[0].isClosed()) {
                socks[0].close();
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    
}