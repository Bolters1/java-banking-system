package org.poo.service.commands;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.BusinessAccount;

import java.util.ArrayList;

public class AddNewBusinessAssociate implements Command{
    String account;
    String role;
    String email;
    int timestamp;
    ArrayList<Users> users;

    public AddNewBusinessAssociate(String account, String role, String email, int timestamp, ArrayList<Users> users) {
        this.account = account;
        this.role = role;
        this.email = email;
        this.timestamp = timestamp;
        this.users = users;
    }

    @Override
    public void execute() {
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount.getIBAN().equals(account)){
                    ((BusinessAccount) userAccount).addAssociate(email, role, user.getName(email, users));
                }
            }

        }

    }
}
