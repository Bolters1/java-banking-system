package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.SavingsAccount;

import java.util.ArrayList;

public class ChangeInterestRate implements Command{
    String account;
    double interestRate;
    ArrayList<Users> users;
    int timestamp;
    public ChangeInterestRate(String account, double interestRate, ArrayList<Users> users, int timestamp) {
        this.account = account;
        this.interestRate = interestRate;
        this.users = users;
        this.timestamp = timestamp;
    }

    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        ObjectNode object = singleton.getMapper().createObjectNode();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if(userAccount.getIBAN().equals(account)){
                    if (userAccount instanceof SavingsAccount) {
                        ((SavingsAccount) userAccount).setInterestRate(interestRate);
                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                        transaction1.put("timestamp", timestamp);
                        transaction1.put("description", "Interest rate of the account changed to " + interestRate);
                        user.getTransactionManager().addTransaction(transaction1);
                        return;
                    }
                    else {
                        outputNode.put("timestamp", timestamp);
                        outputNode.put("description", "This is not a savings account");
                        object.put("command", "changeInterestRate");
                        object.set("output", outputNode);
                        object.put("timestamp", timestamp);

                        singleton.getOutput().add(object);
                    }
                }
            }

        }

    }
}
