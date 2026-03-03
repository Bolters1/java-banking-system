package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;

public class UpgradePlan implements Command{
    String account;
    String newPlan;
    int timestamp;
    ArrayList<Users> users;
    CurrencyGraph graph;

    public UpgradePlan(String account, String newPlan, int timestamp, ArrayList<Users> users, CurrencyGraph graph) {
        this.account = account;
        this.newPlan = newPlan;
        this.timestamp = timestamp;
        this.users = users;
        this.graph = graph;
    }

    @Override
    public void execute() {
        int ok = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if(userAccount.getIBAN().equals(account)){
                    ok = 1;
                    if(!user.getPlan().equals(newPlan)){
                        if(user.getPlan().equals("student") || user.getPlan().equals("standard")){
                            if(newPlan.equals("silver")){
                                if(graph.findExchangeRate(userAccount.getCurrency(), "RON") * userAccount.getBalance() >= 100){
                                    user.setPlan("silver");
                                    userAccount.setBalance(userAccount.getBalance() - 100 * graph.findExchangeRate("RON", userAccount.getCurrency()));
                                    ObjectNode transaction = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction.put("accountIBAN", account);
                                    transaction.put("description", "Upgrade plan");
                                    transaction.put("newPlanType", newPlan);
                                    transaction.put("timestamp", timestamp);
                                    user.getTransactionManager().addTransaction(transaction);
                                }
                                else{
                                    ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction1.put("timestamp", timestamp);
                                    transaction1.put("description", "Insufficient funds");
                                    user.getTransactionManager().addTransaction(transaction1);
                                    return;
                                }
                            }
                            else if(newPlan.equals("gold")){
                                if(graph.findExchangeRate(userAccount.getCurrency(), "RON") * userAccount.getBalance() >= 350){
                                    user.setPlan("gold");
                                    userAccount.setBalance(userAccount.getBalance() - 350 * graph.findExchangeRate("RON", userAccount.getCurrency()));
                                    ObjectNode transaction = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction.put("accountIBAN", account);
                                    transaction.put("description", "Upgrade plan");
                                    transaction.put("newPlanType", newPlan);
                                    transaction.put("timestamp", timestamp);
                                    user.getTransactionManager().addTransaction(transaction);
                                }
                                else{
                                    ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction1.put("timestamp", timestamp);
                                    transaction1.put("description", "Insufficient funds");
                                    user.getTransactionManager().addTransaction(transaction1);
                                    return;
                                }
                            }
                        }
                        else if(user.getPlan().equals("silver")){
                            if(newPlan.equals("gold")){
                                if(graph.findExchangeRate(userAccount.getCurrency(), "RON") * userAccount.getBalance() >= 250){
                                    user.setPlan("gold");
                                    userAccount.setBalance(userAccount.getBalance() - 250 * graph.findExchangeRate("RON", userAccount.getCurrency()));
                                    ObjectNode transaction = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction.put("accountIBAN", account);
                                    transaction.put("description", "Upgrade plan");
                                    transaction.put("newPlanType", newPlan);
                                    transaction.put("timestamp", timestamp);
                                    user.getTransactionManager().addTransaction(transaction);
                                }
                                else{
                                    ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction1.put("timestamp", timestamp);
                                    transaction1.put("description", "Insufficient funds");
                                    user.getTransactionManager().addTransaction(transaction1);
                                    return;
                                }
                            }
                        }
                    }
                    else{
                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                        transaction1.put("timestamp", timestamp);
                        transaction1.put("description", "The user already has the " + newPlan +" plan.");
                        user.getTransactionManager().addTransaction(transaction1);
                        return;
                    }
                }
            }

        }
        if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Account not found");
            object.put("command", "upgradePlan");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }

    }
}
