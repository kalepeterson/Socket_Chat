package client;

import socketChat.Message;
import socketChat.SharedLibrary;
import socketChat.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Connection {
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean isConnd;

	public Connection() {
		sock = null;
		out = null;
		in = null;
		isConnd = false;
	}

	public User connect(byte[] ipOctets) throws Exception {
		User me;
		try {
			InetAddress ia = InetAddress.getByAddress(ipOctets);
			System.out.println("Attempting connection to at " + ia.getHostAddress());
			sock = new Socket(ia, SharedLibrary.DEFAULT_PORT);
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());

			System.out.println("CONNECTED!\n Writing default password.");
			out.writeObject(SharedLibrary.DEFAULT_PASSWORD);

			System.out.println("Reading new port.");
			int port = in.readInt();
			sock.close();

			System.out.println("Attempting new connection at port " + port);
			sock = new Socket(ia, port);
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());

			me = new User("test");
			System.out.println("User " + me.toString() + " created, sending to server.");
			out.writeObject(me);

			//System.out.println("User created and sent.  Reading ID from server.");
			//me.setUniqueID(in.readInt());

			System.out.println("Connection made.  Sending message.");
			sendMessage(new Message(me.getUserName(), "someperson", "Hello someperson"));

			System.out.println("WOW IT WORKED.");
			isConnd = true;
		} catch(Exception e) {
			System.out.println("Connection failed: " + e);
			e.printStackTrace();
			throw e;
		}
		return me;
	}

	public boolean isConnected() {
		return isConnd;
	}

	public void close() {
		try {
			sock.close();
			in = null;
			out = null;
		} catch(Exception e) {
			System.out.println("Socket not closed!" + e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean sendMessage(Message m)
	{
		try {
			out.writeObject(m);
			return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
