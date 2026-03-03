package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.BusinessAccount;

import java.util.ArrayList;

public class DeleteCard implements Command{
    ArrayList<Users> users;
    String email;
    String cardNumber;
    int timestamp;
    public DeleteCard(ArrayList<Users> users, String email, String cardNumber, int timestamp) {
        this.email = email;
        this.cardNumber = cardNumber;
        this.users = users;
        this.timestamp = timestamp;
    }

    @Override
    public void execute() {
        for(int i = 0; i < users.size(); i++){
                for(int j = 0; j < users.get(i).getAccounts().size(); j++){
                    if(users.get(i).getAccounts().get(j) == null)
                        continue;
                    for(int k = 0; k < users.get(i).getAccounts().get(j).getCards().size(); k++){
                        if(users.get(i).getAccounts().get(j).getCards().get(k).getCardNumber().equals(cardNumber)){
                            if(email.equals(users.get(i).getAccounts().get(j).getEmail()) ) {
                                ObjectNode transaction1 = users.get(i).getTransactionManager().getMapper().createObjectNode();
                                transaction1.put("timestamp", timestamp);
                                transaction1.put("description", "The card has been destroyed");
                                transaction1.put("cardHolder", users.get(i).getEmail());
                                transaction1.put("card", users.get(i).getAccounts().get(j).getCards().get(k).getCardNumber());
                                transaction1.put("account", users.get(i).getAccounts().get(j).getIBAN());
                                users.get(i).getTransactionManager().addTransaction(transaction1);
                                users.get(i).getAccounts().get(j).getCards().remove(k);
                                return;
                            }
                            else if(users.get(i).getAccounts().get(j) instanceof BusinessAccount){
                                System.out.println("oipfndsophfopdsopf");
                                ((BusinessAccount)users.get(i).getAccounts().get(j)).deleteCard(email, timestamp, cardNumber, k);
                                for (Users user : users) {
                                    if(user.getEmail().equals(email)){
                                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                        transaction1.put("timestamp", timestamp);
                                        transaction1.put("description", "The card has been destroyed");
                                        transaction1.put("cardHolder", user.getEmail());
                                        transaction1.put("card", cardNumber);
                                        transaction1.put("account", users.get(i).getAccounts().get(j).getIBAN());
                                        users.get(i).getTransactionManager().addTransaction(transaction1);
                                    }
                                }

                            }
                        }
                    }
                }
        }
    }
}
