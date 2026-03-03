package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;
import org.poo.service.users.cards.Card;

import java.util.ArrayList;

public class CheckCardStatus implements Command{
    ArrayList<Users> users;
    String cardNumber;
    int timestamp;

    public CheckCardStatus(ArrayList<Users> users, String cardNumber, int timestamp) {
        this.users = users;
        this.cardNumber = cardNumber;
        this.timestamp = timestamp;
    }

    @Override
    public void execute() {
        int ok = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode object = singleton.getMapper().createObjectNode();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        for (Users user : users) {
            for (Account account : user.getAccounts()) {
                if(account == null)
                    continue;
                for (Card card : account.getCards()) {
                    if(card.getCardNumber().equals(cardNumber)){
                        ok = 1;
                        if(account.getBalance() <= account.getMinimumBalance()){
                            object.put("description", "You have reached the minimum amount of funds, the card will be frozen");
                            object.put("timestamp", timestamp);
                            card.setStatus("frozen");
                            user.getTransactionManager().addTransaction(object);
                        }
                    }
                }

            }

        }
        if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Card not found");
            object.put("command", "checkCardStatus");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }

    }
}
