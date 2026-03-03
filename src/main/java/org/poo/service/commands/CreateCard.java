package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.service.users.accounts.BusinessAccount;
import org.poo.service.users.cards.Card;
import org.poo.service.users.Users;

import java.util.ArrayList;

public class CreateCard implements Command{
    ArrayList<Users> users;
    String iban;
    String email;
    int timestamp;

    public CreateCard(ArrayList<Users> users, String iban, String email, int timestamp) {
        this.users = users;
        this.iban = iban;
        this.email = email;
        this.timestamp = timestamp;
    }

    @Override
    public void execute() {
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getEmail().equals(email)){
                for(int j = 0; j < users.get(i).getAccounts().size(); j++){
                    if(users.get(i).getAccounts().get(j) == null)
                        continue;
                    if(users.get(i).getAccounts().get(j).getIBAN().equals(iban)){
                        if(users.get(i).getEmail().equals(email)) {
                            Card card = new Card();
                            users.get(i).getAccounts().get(j).getCards().add(card);
                            ObjectNode transaction1 = users.get(i).getTransactionManager().getMapper().createObjectNode();
                            transaction1.put("timestamp", timestamp);
                            transaction1.put("description", "New card created");
                            transaction1.put("card", card.getCardNumber());
                            transaction1.put("cardHolder", users.get(i).getEmail());
                            transaction1.put("account", users.get(i).getAccounts().get(j).getIBAN());
                            users.get(i).getTransactionManager().addTransaction(transaction1);
                            return;
                        }
                        else{
                            System.out.println("oipfndsophfopdsopf");
                            String cardNumber = ((BusinessAccount)users.get(i).getAccounts().get(j)).createCard(email, timestamp);
                            for (Users user : users) {
                                if(user.getEmail().equals(email)){
                                    ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                    transaction1.put("timestamp", timestamp);
                                    transaction1.put("description", "New card created");
                                    transaction1.put("card", cardNumber);
                                    transaction1.put("cardHolder", user.getEmail());
                                    transaction1.put("account", users.get(i).getAccounts().get(j).getIBAN());
                                    user.getTransactionManager().addTransaction(transaction1);
                                    return;
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
