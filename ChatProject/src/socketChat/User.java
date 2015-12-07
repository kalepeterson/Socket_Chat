package socketChat;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;
import client.Connection;
import client.Conversation;

/**
 * Created by Kale on 11/15/2015.
 */
public class User implements Serializable{
	public List<Conversation> conversations; // List of all current conversations
	private Connection connection;
    private String userName; //name that appears in chat
    private int uniqueID; //the id number for this user
    private String pmKey; //the character sequence for sending a private message to this user.

    public User(String name) {
        this.uniqueID = -1;
        this.userName = name;
        this.pmKey = String.format("%s%s%d", "@", userName, uniqueID);
        connection = null;
        conversations = new LinkedList<Conversation>();
    }

    public String getUserName() {
        return userName;
    }
    public int getUniqueID() {
        return uniqueID;
    }
    public String getPmKey() {
        return pmKey;
    }
    public void setUserName(String newName) {
        this.userName = newName;
    }
    public void setUniqueID(int id) {
        this.uniqueID = id;
    }
    
    public Connection getConnection() {
    	return connection;
    }
    
    public void setConnection(Connection c) {
    	connection = c;
    }
    
    public void addConversation(Conversation c) {
    	conversations.add(c);
    }
    
    public void addConversation(String contactName) {
    	conversations.add(new Conversation(contactName));
    }

    @Override
    public String toString() {
        return this.pmKey;
    }
}
