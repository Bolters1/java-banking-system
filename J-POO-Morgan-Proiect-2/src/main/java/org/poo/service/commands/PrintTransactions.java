package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;

import java.util.ArrayList;

public class PrintTransactions implements Command {
    int timestamp;
    ArrayList<Users> users;
    String email;

    public PrintTransactions(int timestamp, ArrayList<Users> users, String email) {
        this.timestamp = timestamp;
        this.users = users;
        this.email = email;
    }

    @Override
    public void execute() {
        for (Users user : users) {
            if (user.getEmail().equals(email)) {
                Singleton singleton = Singleton.getInstance();
                ObjectNode object = singleton.getMapper().createObjectNode();
                object.put("command", "printTransactions");

                ArrayList<JsonNode> filteredTransactions = new ArrayList<>();
                for (JsonNode transaction : user.getTransactionManager().getTransactions()) {
                    int transactionTimestamp = transaction.get("timestamp").asInt();
                    if (transactionTimestamp < timestamp) {
                        filteredTransactions.add(transaction);
                    }
                }

                filteredTransactions.sort((t1, t2) -> {
                    int t1Timestamp = t1.get("timestamp").asInt();
                    int t2Timestamp = t2.get("timestamp").asInt();
                    return Integer.compare(t1Timestamp, t2Timestamp);
                });

                object.set("output", singleton.getMapper().valueToTree(filteredTransactions));
                object.put("timestamp", timestamp);
                singleton.getOutput().add(object);
            }
        }
    }

}
