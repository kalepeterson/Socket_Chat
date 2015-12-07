package server;

import client.Conversation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kale on 12/6/2015.
 */
public class DatabaseElement {
        public String clientName;
        public List<Conversation> conversations;

        public DatabaseElement(String name) {
            clientName = name;
            conversations = new LinkedList<Conversation>();
        }
}
