package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.cards.Card;

import java.util.ArrayList;

public class CashWithdrawal implements Command{
    String cardNumber;
    double amount;
    String email;
    String location;
    int timestamp;
    ArrayList<Users> users;
    CurrencyGraph graph;

    public CashWithdrawal(String cardNumber, double amount, String email, String location, int timestamp, ArrayList<Users> users, CurrencyGraph graph) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.email = email;
        this.location = location;
        this.timestamp = timestamp;
        this.users = users;
        this.graph = graph;
    }

    @Override
    public void execute() {
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        int ok = 0;
        for (Users user : users) {
            for (Account account : user.getAccounts()) {
                if(account == null)
                    continue;
                for (Card card : account.getCards()) {
                    if(card == null)
                        continue;
                    if(card.getCardNumber().equals(cardNumber)){
                        ok = 1;
                        if(graph.findExchangeRate(account.getCurrency(), "RON") * account.getBalance() < amount){
                            object.put("description", "Insufficient funds");
                            object.put("timestamp", timestamp);
                            user.getTransactionManager().addTransaction(object);
                            return;
                        }
                        else if(card.getStatus().equals("frozen")){
                            object.put("description", "The card is frozen");
                            object.put("timestamp", timestamp);
                            user.getTransactionManager().addTransaction(object);
                            return;
                        }
                        else{
                            account.setBalance(account.getBalance() - graph.findExchangeRate("RON", account.getCurrency()) * amount- user.getCommision(amount) * amount * graph.findExchangeRate("RON", account.getCurrency()));
                            ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                            transaction1.put("timestamp", timestamp);
                            transaction1.put("description", "Cash withdrawal of " + amount);
                            transaction1.put("amount", amount);
                            user.getTransactionManager().addTransaction(transaction1);
                        }
                    }
                }

            }

        }
        if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Card not found");
            object.put("command", "cashWithdrawal");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }

    }
}
