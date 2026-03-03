package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;

import java.util.ArrayList;

public class PrintUsers implements Command{
    ArrayList<Users> users;
    int timestamp;
    public PrintUsers(ArrayList<Users> users, int timestamp){
        this.users = users;
        this.timestamp = timestamp;
    }
    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        ObjectNode object = singleton.getMapper().createObjectNode();
        object.put("command", "printUsers");
        object.put("output", singleton.getMapper().valueToTree(users));
        object.put("timestamp", timestamp);
        singleton.getOutput().add(object);
    }
}
