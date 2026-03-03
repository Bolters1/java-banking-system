package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.cashback.CashbackSelector;
import org.poo.service.cashback.NrOfTransactionsStrategy;
import org.poo.service.cashback.SpendingThreshold;
import org.poo.service.commerciants.Commerciants;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.accounts.ClassicAccount;

import java.util.ArrayList;

public class SendMoney implements Command{
    String account;
    String receiver;
    double amount;
    String description;
    String email;
    ArrayList<Users> users;
    CurrencyGraph graph;
    int timestamp;
    Account useerAccount = null;
    int ok = 0;
    int checkAccount = 0;
    ArrayList<Commerciants> commerciants;
    public SendMoney(String account, String receiver, double amount, String description, String email, ArrayList<Users> users, CurrencyGraph graph, int timetamp, ArrayList<Commerciants> commerciants) {
        this.account = account;
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
        this.email = email;
        this.users = users;
        this.graph = graph;
        this.timestamp = timetamp;
        this.commerciants = commerciants;
    }

    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        String currencySender = new String();
        int insufficientFunds = 0;
        Users sender = new Users("", "", "","","");
        for (Users user : users) {
                for (Account userAccount : user.getAccounts()) {
                    if(userAccount == null)
                        continue;
                    if(userAccount.getIBAN().equals(account)){
                        checkAccount = 1;
                        ok = 1;
                        sender = user;
                        if(userAccount.getBalance() - sender.getCommision(graph.findExchangeRate(userAccount.getCurrency(), "RON") * amount) * amount >= amount) {
                            currencySender = userAccount.getCurrency();
                            useerAccount = userAccount;

                        }
                        else {
                            insufficientFunds = 1;
                        }
                    }
                }


        }
        if(checkAccount == 0 || receiver == ""){
                outputNode.put("timestamp", timestamp);
                outputNode.put("description", "User not found");
                object.put("command", "sendMoney");
                object.set("output", outputNode);
                object.put("timestamp", timestamp);

                singleton.getOutput().add(object);
                return;

        }
        checkAccount = 0;
        if(insufficientFunds == 1){
            ObjectNode transaction1 = sender.getTransactionManager().getMapper().createObjectNode();
            transaction1.put("timestamp", timestamp);
            transaction1.put("description", "Insufficient funds");
            sender.getTransactionManager().addTransaction(transaction1);
            return;
        }
        if(ok == 1)
        for(Users user : users){
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if(userAccount.getIBAN().equals(receiver)){
                    checkAccount = 1;
                    ObjectNode transaction2 = user.getTransactionManager().getMapper().createObjectNode();

                    ObjectNode transaction = sender.getTransactionManager().getMapper().createObjectNode();
                    transaction.put("timestamp", timestamp);
                    transaction.put("description", description);
                    transaction.put("senderIBAN", account);
                    transaction.put("receiverIBAN", receiver);
                    transaction.put("amount",amount + " " + currencySender);
                    transaction.put("transferType", "sent");
                    sender.getTransactionManager().addTransaction(transaction);
                    if(graph.findExchangeRate(currencySender, userAccount.getCurrency()) != null) {
                        userAccount.setBalance(userAccount.getBalance() + amount * graph.findExchangeRate(currencySender, userAccount.getCurrency()));
//                        useerAccount.setBalance(useerAccount.getBalance() - amount - sender.getCommision() * amount);
                        useerAccount.setBalance(useerAccount.getBalance() - amount - sender.getCommision(graph.findExchangeRate(currencySender, "RON") * amount) * amount);
                        if(user.getPlan().equals("silver") && amount * graph.findExchangeRate(currencySender, "RON") >= 300){
                            sender.checkForGold();
                        }
                        transaction2.put("timestamp", timestamp);
                        transaction2.put("description", description);
                        transaction2.put("senderIBAN", account);
                        transaction2.put("receiverIBAN", receiver);
                        transaction2.put("amount", amount * graph.findExchangeRate(currencySender, userAccount.getCurrency()) + " " + userAccount.getCurrency());
                        transaction2.put("transferType", "received");
                        user.getTransactionManager().addTransaction(transaction2);
                    }
                    else{
                        userAccount.setBalance(userAccount.getBalance() + amount);
//                        useerAccount.setBalance(useerAccount.getBalance() - amount - sender.getCommision() * amount);
                        useerAccount.setBalance(useerAccount.getBalance() - amount);
                        transaction2.put("timestamp", timestamp);
                        transaction2.put("description", description);
                        transaction2.put("senderIBAN", account);
                        transaction2.put("receiverIBAN", receiver);
                        transaction2.put("amount",amount + " " + currencySender);
                        transaction2.put("transferType", "received");
                        user.getTransactionManager().addTransaction(transaction2);
                    }
                    return;
                }
            }

        }
        for (Commerciants commerciant : commerciants) {
            if(commerciant.getAccount().equals(receiver)){
                checkAccount = 1;
                CashbackSelector selector = new CashbackSelector();
                int cashback = useerAccount.getCashback().useDiscount(commerciant.getType());
                //ObjectNode transaction2 = user.getTransactionManager().getMapper().createObjectNode();
                if(insufficientFunds == 1){
                    ObjectNode transaction1 = sender.getTransactionManager().getMapper().createObjectNode();
                    transaction1.put("timestamp", timestamp);
                    transaction1.put("description", "Insufficient funds");
                    sender.getTransactionManager().addTransaction(transaction1);
                    return;
                }

                ObjectNode transaction = sender.getTransactionManager().getMapper().createObjectNode();
                transaction.put("timestamp", timestamp);
                transaction.put("description", description);
                transaction.put("senderIBAN", account);
                transaction.put("receiverIBAN", receiver);
                transaction.put("amount",amount + " " + currencySender);
                transaction.put("transferType", "sent");
                sender.getTransactionManager().addTransaction(transaction);
//                    userAccount.setBalance(userAccount.getBalance() + amount);
                useerAccount.setBalance(useerAccount.getBalance() - amount - sender.getCommision(graph.findExchangeRate(useerAccount.getCurrency(), "RON") * amount) * amount + cashback * amount / 100);
                if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {
                    selector.setStrategy(new NrOfTransactionsStrategy());
                    selector.executeCashback(useerAccount, commerciant.getCommerciant(), amount, amount, null);
                } else {
                    selector.setStrategy(new SpendingThreshold());
                    selector.executeCashback(useerAccount, commerciant.getCommerciant(), amount, graph.findExchangeRate(useerAccount.getCurrency(), "RON") * amount, sender);
                }
                if (sender.getPlan().equals("silver") && amount * graph.findExchangeRate(useerAccount.getCurrency(), "RON") >= 300) {
                    sender.checkForGold();
                }
                return;
            }
        }

        if(checkAccount == 0){
                outputNode.put("timestamp", timestamp);
                outputNode.put("description", "User not found");
                object.put("command", "sendMoney");
                object.set("output", outputNode);
                object.put("timestamp", timestamp);

                singleton.getOutput().add(object);
                return;
        }

    }
}
