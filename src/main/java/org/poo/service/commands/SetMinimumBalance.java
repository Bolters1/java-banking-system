package org.poo.service.commands;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;

public class SetMinimumBalance implements Command{
    ArrayList<Users> users;
    String account;
    double amount;

    public SetMinimumBalance(ArrayList<Users> users, String account, double amount) {
        this.users = users;
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if(userAccount.getIBAN().equals(account)){
                    userAccount.setMinimumBalance(amount);
                    return;
                }
            }

        }

    }
}
