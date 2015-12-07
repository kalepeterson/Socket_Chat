package client;

import socketChat.Message;
import socketChat.SharedLibrary;
import socketChat.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Connection {
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean isConnected;

	public Connection() {
		sock = null;
		out = null;
		in = null;
		isConnected = false;
	}

	/**
	 * Attempts to connect to the server.
	 * @param ipOctets - Byte array containing the 4 IPv4 octets.
	 * @return User pertaining to the connection, null if failed.
	 * @throws Exception - Failed connection.
	 */
	public User connect(byte[] ipOctets, String userName) throws Exception {
		User me;
		try {
			InetAddress ia = InetAddress.getByAddress(ipOctets);
			System.out.println("Attempting connection to " + ia.getHostAddress());
			sock = new Socket(ia, SharedLibrary.DEFAULT_PORT);
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());

			System.out.println("Successful connection.\n Writing default password.");
			out.writeObject(SharedLibrary.DEFAULT_PASSWORD);

			System.out.println("Reading new port.");
			int port = in.readInt();
			sock.close();

			System.out.println("Attempting new connection at port " + port);
			sock = new Socket(ia, port);
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());

			me = new User(userName);
			System.out.println("User " + me.toString() + " created, sending to server.");
			out.writeObject(me);

			//System.out.println("User created and sent.  Reading ID from server.");
			//me.setUniqueID(in.readInt());

			System.out.println("Connection made.  Sending message.");
			tellServer(new Message(me.getUserName(), "someperson", "Hello someperson"));

			isConnected = true;
		} catch(Exception e) {
			// Connection failed, throw exception
			throw e;
		}
		return me;
	}

	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Closes the connection
	 */
	public void close() {
		try {
			sock.close();
			in = null;
			out = null;
		} catch(Exception e) {
			System.out.println("ERROR: Socket not closed!" + e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Conversation> listenToServer() {
		try {
			List<Conversation> result = new LinkedList<Conversation>();
			Conversation c = null;
			while((c = (Conversation)in.readObject()) != null) {
				result.add(c);
			}
			return result;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Tells the server that the client needs its conversations.
	 * @return True if successful, false otherwise.
	 */
	public boolean tellServer(String clientName)
	{
		try {
			out.writeObject(new ChatCommand(enmCommand.READMESSAGES, new Message(clientName, "", "")));
			return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Tells the server that the client is sending a message and sends it.
	 * @param m - The message to send.
	 * @return True if successful, false otherwise.
	 */
	public boolean tellServer(Message m)
	{
		try {
			out.writeObject(new ChatCommand(m));
			return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
