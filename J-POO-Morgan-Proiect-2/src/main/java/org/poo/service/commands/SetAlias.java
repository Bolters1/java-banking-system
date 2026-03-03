package org.poo.service.commands;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;

public class SetAlias implements Command{
    String email;
    String account;
    String alias;
    ArrayList<Users> users;

    public SetAlias(String email, String account, String alias, ArrayList<Users> users) {
        this.email = email;
        this.account = account;
        this.alias = alias;
        this.users = users;
    }

    @Override
    public void execute() {
        for (Users user : users) {
            if(user.getEmail().equals(email)){
                for (Account userAccount : user.getAccounts()) {
                    if(userAccount.getIBAN().equals(account))
                        userAccount.setAlias(alias);
                }

            }
        }

    }
}
