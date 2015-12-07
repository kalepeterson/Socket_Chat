package socketChat;

import java.io.Serializable;

public class Message implements Serializable{
	private String sender;
	private String receiver;
	private String message;
	private Boolean isSend;
	
	public Message(String s) {
		sender = s;
		receiver = "";
		message = "";
		isSend = false;
	}
	
	public Message(String s, String r, String m) {
		sender = s;
		receiver = r;
		message = m;
		isSend = true;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Boolean getIsSend() {
		return isSend;
	}
	
	public String toString() {
		String string = "";
		string += "From: " + sender + " To: " + receiver + " Message: " + message + "\n";
		return string;
	}
}
