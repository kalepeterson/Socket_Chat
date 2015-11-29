package client;

import socketChat.SharedLibrary;
import socketChat.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
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
        byte[] ip = new byte[4];
        String[] octets;
        while(!valid) {
            System.out.println("Format: ###.###.###.###");
            input = stdin.next(); //next because the entire input should be one token
            try {
                if(!input.contains(".")) {
                    System.out.println("Input must contain a period (.)! Try again:");
                    continue;
                }
                octets = input.split(Pattern.quote(".")); //breaks input into String array where . is found
                if(octets.length != 4) {
                    System.out.println("Invalid number of octets! Try again:");
                    continue;
                }
                for(int i = 0; i < 4; i++) {
                    ip[i] = (byte)(Integer.parseUnsignedInt(octets[i]));
                }
                valid = true;
            } catch(Exception e) {
                System.out.println(e);
                System.out.println("Try again:");
            }
        }
        try {
            InetAddress ia = InetAddress.getByAddress(ip);
            System.out.println("Attempting connection to " + ia.getHostName() + " at " + ia.getHostAddress());
            Socket sock = new Socket(ia, SharedLibrary.DEFAULT_PORT);
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());

            System.out.println("CONNECTED!\n Writing default password.");
            out.writeObject(SharedLibrary.DEFAULT_PASSWORD);

            System.out.println("Reading new port.");
            int port = in.readInt();
            sock.close();

            System.out.println("Attempting new connection at port " + port);
            sock = new Socket(ia, port);
            in = new ObjectInputStream(sock.getInputStream());
            out = new ObjectOutputStream(sock.getOutputStream());

            User me = new User("test");
            System.out.println("User " + me.toString() + " created, sending to server.");
            out.writeObject(me);

            System.out.println("User created and sent.  Reading ID from server.");
            me.setUniqueID(in.readInt());

            System.out.println("WOW IT WORKED.");
            String greet = (String)(in.readObject());
            System.out.println("Greeting from server: " + greet);
            sock.close();
        } catch(Exception e) {
            System.out.println("Connection failed: " + e);
            e.printStackTrace();
        }
    }
}
