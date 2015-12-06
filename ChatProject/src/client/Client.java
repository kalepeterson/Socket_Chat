package client;

import socketChat.Message;
import socketChat.User;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Kale on 11/15/2015.
 */
public class Client {
    public static void main(String[] args) {
        System.out.println("I'm a client!");
        //below should probably be put into a method but I wanted to scrap it out first.
        System.out.println("Please enter an IPv4 address of the Socket_Chat server:");
        boolean valid = false;
        Scanner stdin = new Scanner(System.in);
        String input;
        Connection conn = new Connection();
        byte[] ipOctets = new byte[4];
        String[] octets;
        User me = null;
        while (!valid) {
            System.out.println("Format: ###.###.###.###");
            input = stdin.next(); //next because the entire input should be one token
            try {
                if (!input.contains(".")) {
                    System.out.println("Input must contain a period (.)! Try again:");
                    continue;
                }
                octets = input.split(Pattern.quote(".")); //breaks input into String array where . is found
                if (octets.length != 4) {
                    System.out.println("Invalid number of octets! Try again:");
                    continue;
                }
                for (int i = 0; i < 4; i++) {
                    ipOctets[i] = (byte) (Integer.parseUnsignedInt(octets[i]));
                }
                valid = true;
                System.out.println("Valid ip, attempting connection.");
                me = conn.connect(ipOctets);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Try again:");
                me = null;
            }
        }
        if(me == null) {
            System.out.println("User is null.  Exiting.");
            System.exit(0);
        }
        System.out.println("Connection successful from Client!");
        //Sam's client
        int choice = 0;
        int unreadSaved = 0;
        int totalSaved = 0;
        int recMessCounter = 0;
        int totalRecMess = 0;
        int saveYN = 0;
        String message;
        String destination;
        String clientID = me.getUserName();
        LinkedList<Message> recMessage = new LinkedList<Message>();
        LinkedList<Message> savedMessages = new LinkedList<Message>();
        do
        {
            System.out.printf("Choose an option\n 1: Send a message\n 2: Look at messages\n 3: Exit");

            choice = stdin.nextInt();
            switch(choice)
            {
                case 1:
                    System.out.printf("Enter the client ID for the recipient.");
                    destination = stdin.nextLine();
                    System.out.printf("Enter message to be sent.\n");
                    message = stdin.nextLine();
                    Message m = new Message(message, destination, me.getUserName());
                    recMessage.add(m);
                    conn.sendMessage(m);
                    break;
                case 2:
                    System.out.printf("You have %d unread messages and %d saved messages",totalRecMess,totalSaved);
                    do
                    {
                        System.out.printf("Would you like to look at unread or saved messages?\n 1: unread\n 2: Saved\n");
                        unreadSaved = stdin.nextInt();
                    }
                    while((unreadSaved !=1)||(unreadSaved !=2));
                    if(unreadSaved == 1)
                    {
                        System.out.printf("What message would you like to look at?");
                        unreadSaved = stdin.nextInt();
                        //parse and print message?
                        System.out.printf("%s\n",recMessage.get(unreadSaved));
                        System.out.printf("Would you like to save the Message\n 1: Yes\n 2:No");
                        saveYN = stdin.nextInt();
                        if(saveYN == 1)
                        {
                            savedMessages.add(recMessage.get(unreadSaved));
                        }
                        recMessage.remove(unreadSaved);
                    }
                    else
                    {
                        System.out.printf("What message would you like to look at?");
                        unreadSaved = stdin.nextInt();
                    }
                    break;
            }

        }while(choice!=3);
        conn.close();
    }
}
