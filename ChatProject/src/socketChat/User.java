package socketChat;

import java.io.Serializable;

/**
 * Created by Kale on 11/15/2015.
 */
public class User implements Serializable{
    private String userName; //name that appears in chat
    private int uniqueID; //the id number for this user
    private String pmKey; //the character sequence for sending a private message to this user.

    public User(String name) {
        this.uniqueID = -1;
        this.userName = name;
        this.pmKey = String.format("%s%s%d", "@", userName, uniqueID);
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
    public void setUserName(String newName) throws Exception {
        if(newName != null && newName.length() > 2) {
            this.userName = newName;
        } else {
            throw new Exception("Username cannot be null or less than two characters in length.");
        }
    }
    public void setUniqueID(int id) {
        this.uniqueID = id;
    }

    @Override
    public String toString() {
        return this.pmKey;
    }
}
