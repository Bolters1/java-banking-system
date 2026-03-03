package org.poo.service.commands;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.BusinessAccount;

import java.util.ArrayList;

public class ChangeDepositLimit implements Command{
    String email;
    String account;
    double amount;
    int timestamp;
    ArrayList<Users> users;
    public ChangeDepositLimit(String email, String account, double amount, int timestamp, ArrayList<Users> users) {
        this.email = email;
        this.account = account;
        this.amount = amount;
        this.timestamp = timestamp;
        this.users = users;
    }
    @Override
    public void execute() {
        for (Users user : users) {
            if(user.getEmail().equals(email)) {
                for (Account userAccount : user.getAccounts()) {
                    if(userAccount.getIBAN().equals(account)){
                        ((BusinessAccount)userAccount).setDepositLimit(amount);
                    }
                }
            }
        }

    }
}
