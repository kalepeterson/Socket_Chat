package client;

import java.io.Serializable;
import socketChat.Message;

/**
 * Created by Kale on 12/5/2015.
 */
public class ChatCommand implements Serializable {

    public enmCommand cmdType;
    public Message msg;

    public ChatCommand() {
        cmdType = enmCommand.INCORRECT;
        msg = null;
    }
    
    /**
     * Constructs a ChatCommand with the passed enum command
     * @param cmd - Enumerated command.
     */
    public ChatCommand(enmCommand cmd) {
        cmdType = cmd;
        msg = null;
    }
    
    /**
     * Constructs a ChatCommand with the passed enum command
     * @param cmd - Enumerated command.
     * @param msg - Specified message to send the server.
     */
    public ChatCommand(enmCommand cmd, Message msg) {
    	cmdType = cmd;
    	this.msg = msg;
    }
    
    /**
     * Constructs a ChatCommand with the passed Message
     * @param m - Message to send.
     */
    public ChatCommand(Message m) {
        cmdType = enmCommand.SENDMESSAGE;
        msg = m;
    }

    public boolean isValid() {
        return cmdType != enmCommand.INCORRECT;
    }

    @Override
    public String toString() {
        return msg.toString();
    }
}