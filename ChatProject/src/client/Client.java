package client;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import socketChat.Message;
import socketChat.User;

public class Client {
	/**
	 * Attempts to connect to a server based on user-provided server
	 * information.
	 * 
	 * @param input
	 *            - Scanner object for input.
	 * @return User - Returns a connected client, or null if connection failed.
	 */
	public static User connect(Scanner input, String userName) {
		User client = null;
		Connection conn = new Connection();
		String[] octets;
		String inputString;
		boolean invalid = true;
		byte[] ipOctets = new byte[4];

		System.out
				.println("Please enter an IPv4 address of the Socket_Chat server:");
		do {
			System.out.println("Format: ###.###.###.###");
			inputString = input.next(); // next because the entire input should
										// be one token
			try {
				if (!inputString.contains(".")) {
					System.out
							.println("ERROR: Input must contain a period (.)! Try again:");
					continue;
				}
				octets = inputString.split(Pattern.quote(".")); // breaks input
																// into String
																// array where .
																// is found
				if (octets.length != 4) {
					System.out
							.println("ERROR: Invalid number of octets. Try again:");
					continue;
				}
				for (int i = 0; i < 4; i++) {
					ipOctets[i] = (byte) (Integer.parseInt(octets[i]));
				}
				invalid = false;
				System.out.println("Valid IP, attempting connection.");
				client = conn.connect(ipOctets, userName);
				client.setConnection(conn);
			} catch (Exception e) {
				System.out.println("ERROR: Connection failed.");
				// Clear input
				input.nextLine();
				client = null;
			}
		} while (invalid);

		// Return results
		return client;
	}

	public static String getClientName(Scanner input) {
		String clientName = "";
		// Get client name
		while (clientName.equals("")) {
			System.out.print("Please enter your name: ");
			clientName = input.nextLine();
			if (!clientName.equals("")) {
				System.out
						.println("Welcome to the Instant Messaging Application, "
								+ clientName + ".");
			}
		}
		return clientName;
	}

	/**
	 * Runs the client menu.
	 * 
	 * @param input
	 *            - Scanner object for user input.
	 */
	public static void runMenu(Scanner input) {
		int inputSelection = 0;
		String message = "";
		String destination = "";
		String clientName = "";
		User client = null;
		boolean shouldContinue = true;

		do {
			// Get the client's name
			if (clientName.equals("")) {
				clientName = getClientName(input);
			}

			// Run menu
			switch (inputSelection) {
			case 0:
				// Display menu
				displayMenu();

				// Get user selection (handle invalid inputs)
				try {
					inputSelection = Integer.parseInt(input.nextLine());
				} catch (NumberFormatException ex) {
					inputSelection = 0;
					System.out
							.println("ERROR: Invalid selection. Please select a valid option.");
				}
				break;
			case 1:
				// Attempt to connect
				if (client == null) {
					client = connect(input, clientName);
				} else {
					System.out.println("You are already connected!");
				}
				inputSelection = 0;
				break;
			case 2:
				// View
				if (client != null) {
					do {

						// Tell server to send conversations
						client.getConnection().tellServer(clientName);

						// Get most recent version of conversations from server
						List<Conversation> cList = client.getConnection().listenToServer();
						if (cList != null) {
							System.out.println(cList.size() + " conversations");
							client.conversations = cList;
						}

						// Display submenu options
						if (client.conversations.size() == 0) {
							System.out.println("No conversations to view.");
							shouldContinue = false;
						} else {
							System.out
									.println("Current Conversations:\nPlease select a conversation (or -1 to quit)");
							for (int i = 0; i < client.conversations.size(); i++) {
								System.out.println("\t"
												+ (i + 1)
												+ ": "
												+ client.conversations.get(i)
														.getName());
							}
							System.out.print("Selection: ");
							// Get selection
							try {
								inputSelection = Integer.parseInt(input
										.nextLine());
								shouldContinue = false;
							} catch (NumberFormatException ex) { // Handle
																	// invalid
																	// input
								System.out
										.println("ERROR: Invalid selection. Please try again.");
								shouldContinue = true;
							}

							// Handle out of bounds input
							if (inputSelection > client.conversations.size()) {
								System.out
										.println("ERROR: Invalid selection. Please try again.");
								shouldContinue = true;
							}

							// Handle quit
							if (inputSelection == -1) {
								shouldContinue = false;
							}

							// If valid selection, display conversation
							if (inputSelection != -1) {
								System.out.println(client.conversations
										.get(inputSelection - 1));
							}
						}
					} while (shouldContinue);
				} else {
					System.out
							.println("ERROR: Not connected. Please connect to a server.");
				}
				inputSelection = 0;
				break;
			case 3:
				// Send
				if (client != null) {
					System.out.println("Enter the name of the recipient.");
					destination = input.nextLine();
					System.out.println("Enter message to be sent.");
					message = input.nextLine();
					Message m = new Message(client.getUserName(), destination,
							message);

					client.getConnection().tellServer(m);

				} else {
					System.out
							.println("ERROR: Not connected. Please connect to a server.");
				}
				inputSelection = 0;
				break;
			default:
				if (client != null) {
					// Close connection
					client.getConnection().close();
				}
				break;
			}
		} while (inputSelection != 4);
	}

	/**
	 * Displays the client menu
	 */
	public static void displayMenu() {
		int i = 1;
		System.out.println("What would you like to do?");
		System.out.println("\t" + i++ + ": Connect");
		System.out.println("\t" + i++ + ": View Messages");
		System.out.println("\t" + i++ + ": Send Message");
		System.out.println("\t" + i++ + ": Exit");
		System.out.print("Selection: ");
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);

		runMenu(input);
	}
}
