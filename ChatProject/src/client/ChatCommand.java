package client;

import socketChat.Message;

/**
 * Created by Kale on 12/5/2015.
 */
public class ChatCommand {

    public enmCommand cmdType;
    public Message msg;

    public ChatCommand() {
        cmdType = enmCommand.INCORRECT;
        msg = null;
    }
    public ChatCommand(enmCommand cmd) {
        cmdType = cmd;
        msg = null;
    }
    public ChatCommand(Message m) {
        cmdType = enmCommand.SENDMESSAGE;
        msg = m;
    }

    public boolean isValid() {
        return cmdType != enmCommand.INCORRECT;
    }
}