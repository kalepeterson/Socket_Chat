package client;

import socketChat.Message;

import java.util.LinkedList;
import java.util.List;

public class Conversation {
	private String contactName;
	private List<Message> messages;
	
	public Conversation(String cName) {
		contactName = cName;
		messages = new LinkedList<Message>();
	}
	
	public String getName() {
		return contactName;
	}
	
	public void addMessage(Message m) {
		messages.add(m);
	}
	
	public Message getMessage(int i) {
		return messages.get(i);
	}
	
	public String toString() {
		String string = "";
		string += "Contact: " + contactName + "\nMessages:\n";
		if(messages.size() == 0) {
			System.out.println("\tNo messages.");
		} else {
			for(int i = 0; i < messages.size(); i++) {
				string += "\t" + messages.get(i).getMessage() + "\n";
			}
		}
		return string;
	}
}
