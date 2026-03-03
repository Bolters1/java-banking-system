package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;

public class WithdrawSavings implements Command{
    ArrayList<Users> users;
    String account;
    double amount;
    String currency;
    int timestamp;
    int ok = 0;

    public WithdrawSavings(ArrayList users, String account, double amount, String currency, int timestamp) {
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.users = users;
    }

    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if(userAccount.getIBAN().equals(account)){
                    if(!userAccount.getType().equals("savings")){
                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                        transaction1.put("timestamp", timestamp);
                        transaction1.put("description", "Account is not of type savings.");
                        user.getTransactionManager().addTransaction(transaction1);
                        return;
                    }
                    if(user.getAge() < 21){
                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                        transaction1.put("timestamp", timestamp);
                        transaction1.put("description", "You don't have the minimum age required.");
                        user.getTransactionManager().addTransaction(transaction1);
                        return;
                    }
                    for (Account account1 : user.getAccounts()) {
                        if(account1.getCurrency().equals(currency) && account1.getType().equals("classic")){
                            ok = 1;
                            if(amount > userAccount.getBalance()){
                                ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                transaction1.put("timestamp", timestamp);
                                transaction1.put("description", "Insufficient funds");
                                user.getTransactionManager().addTransaction(transaction1);
                                return;
                            }
                            ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                            transaction1.put("timestamp", timestamp);
                            transaction1.put("description", "Savings withdrawal");
                            transaction1.put("classicAccountIBAN", account1.getIBAN());
                            transaction1.put("savingsAccountIBAN", userAccount.getIBAN());
                            transaction1.put("amount", amount);
                            user.getTransactionManager().addTransaction(transaction1);
                            user.getTransactionManager().addTransaction(transaction1);
                            userAccount.setBalance(userAccount.getBalance() - amount);
                            account1.setBalance(account1.getBalance() + amount);
                            return;
                        }
                    }
                    if(ok == 0){
                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                        transaction1.put("timestamp", timestamp);
                        transaction1.put("description", "You do not have a classic account.");
                        user.getTransactionManager().addTransaction(transaction1);
                    }

                }
            }

        }

    }
}
