package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.BusinessAccount;

import java.util.ArrayList;

public class ChangeSpendingLimit implements Command{
    String email;
    String account;
    double amount;
    int timestamp;
    ArrayList<Users> users;
    public ChangeSpendingLimit(String email, String account, double amount, int timestamp, ArrayList<Users> users) {
        this.email = email;
        this.account = account;
        this.amount = amount;
        this.timestamp = timestamp;
        this.users = users;
    }

    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        int ok = 0;
        for (Users user : users) {
            if(user.getEmail().equals(email)) {
                for (Account userAccount : user.getAccounts()) {
                    if(userAccount.getIBAN().equals(account)){
                        ok = 1;
                        if(userAccount instanceof BusinessAccount)
                            ((BusinessAccount)userAccount).setSpendingLimit(amount);
                    }
                }
            }

        }
        if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "You must be owner in order to change spending limit.");
            object.put("command", "changeSpendingLimit");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }

    }
}
