package org.poo.service.commands;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.BusinessAccount;

import java.util.ArrayList;

public class AddFunds implements Command{
    ArrayList<Users> users;
    String account;
    double amount;
    String email;
    int timestamp;
    public AddFunds(ArrayList<Users> users, String account, double amount, String email, int timestamp) {
        this.users = users;
        this.account = account;
        this.amount = amount;
        this.email = email;
        this.timestamp = timestamp;
    }

    @Override
    public void execute() {
        for(int i = 0; i < users.size(); i++){
            for(int j = 0; j < users.get(i).getAccounts().size(); j++){
                if(users.get(i).getAccounts().get(j) == null)
                    continue;
                if(users.get(i).getAccounts().get(j).getIBAN().equals(account)){
                    if(users.get(i).getEmail().equals(email))
                        users.get(i).getAccounts().get(j).setBalance(users.get(i).getAccounts().get(j).getBalance() + amount);
                    else{
                        ((BusinessAccount)users.get(i).getAccounts().get(j)).addFunds(email, amount, timestamp);
                    }
                    return;
                }
            }
        }
    }
}
