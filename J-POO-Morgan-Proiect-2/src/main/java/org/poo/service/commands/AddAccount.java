package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.AccountFactory;
import org.poo.service.users.accounts.BusinessAccount;

import javax.swing.plaf.ColorUIResource;
import java.util.ArrayList;

public class AddAccount implements Command {
    ArrayList<Users> users;
    String email;
    String currency;
    String accountType;
    int timestamp;
    double interestRate;
    CurrencyGraph graph;
    public AddAccount(ArrayList<Users> users, String email, String currency, String accountType, int timestamp, double interestRate, CurrencyGraph graph) {
        this.users = users;
        this.email = email;
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.interestRate = interestRate;
        this.graph = graph;
    }
    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getEmail().equals(email)){
                if(accountType.equals("classic")){
                    Account account = AccountFactory.createAccount(email, currency, accountType);
                    users.get(i).getAccounts().add(account);
                    ObjectNode transaction1 = users.get(i).getTransactionManager().getMapper().createObjectNode();
                    transaction1.put("timestamp", timestamp);
                    transaction1.put("description", "New account created");
                    users.get(i).getTransactionManager().addTransaction(transaction1);
                    break;
                }
                else if (accountType.equals("savings")){
                    Account savingsAccount =  AccountFactory.createAccount(email, currency, accountType, interestRate);
                    users.get(i).getAccounts().add(savingsAccount);
                    ObjectNode transaction1 = users.get(i).getTransactionManager().getMapper().createObjectNode();
                    transaction1.put("timestamp", timestamp);
                    transaction1.put("description", "New account created");
                    users.get(i).getTransactionManager().addTransaction(transaction1);
                    break;
                }
                else{
                    Account businessAccount = AccountFactory.createAccount(email, currency, accountType);
                    ((BusinessAccount)businessAccount).initiate(graph);
                    users.get(i).getAccounts().add(businessAccount);
                    ObjectNode transaction1 = users.get(i).getTransactionManager().getMapper().createObjectNode();
                    transaction1.put("timestamp", timestamp);
                    transaction1.put("description", "New account created");
                    users.get(i).getTransactionManager().addTransaction(transaction1);
                    break;
                }
            }
        }

    }
}
