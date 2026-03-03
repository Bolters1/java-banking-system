package org.poo.service.users.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.cards.Card;

import java.util.ArrayList;

public class BusinessAccount extends Account {
    @JsonIgnore
    private final ArrayList<BusinessAssociate> associates;
    @JsonIgnore
    private double spendingLimit;
    @JsonIgnore
    private double depositLimit;

    public BusinessAccount(String email, String currency, String type) {
        super(email, currency, type);
        spendingLimit = 500;
        depositLimit = 500;
        associates = new ArrayList<>();
    }
    public void initiate(CurrencyGraph graph){
        spendingLimit = spendingLimit  * graph.findExchangeRate("RON", getCurrency());
        depositLimit = depositLimit  * graph.findExchangeRate("RON", getCurrency());
    }

    public void addAssociate(String email, String role, String name) {
        BusinessAssociate associate = new BusinessAssociate(email, role, name);
        associates.add(associate);
    }

    public void setDepositLimit(double depositLimit) {
        this.depositLimit = depositLimit;
    }

    public void setSpendingLimit(double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public void addFunds(String email, double amount, int timestamp) {
        for (BusinessAssociate associate : associates) {
            if (associate.getEmail().equals(email)) {
                if (associate.getRole().equals("manager")) {
                    setBalance(getBalance() + amount);
                    associate.setTotalDeposited(associate.getTotalDeposited() + amount);
                    ObjectNode transaction = associate.getTransactionManager().getMapper().createObjectNode();
                    transaction.put("email", email);
                    transaction.put("description", "Deposit");
                    transaction.put("amount", amount);
                    transaction.put("timestamp", timestamp);
                    transaction.put("account", getIBAN());
                    associate.getTransactionManager().addTransaction(transaction);
                } else {
                    if (amount <= depositLimit) {
                        setBalance(getBalance() + amount);
                        associate.setTotalDeposited(associate.getTotalDeposited() + amount);
                    }
                }
            }
        }

    }

    public String createCard(String email, int timestamp) {
        for (BusinessAssociate associate : associates) {
            if (associate.getEmail().equals(email)) {
                if (associate.getRole().equals("manager")) {
                    Card card = new Card();
                    getCards().add(card);
                    ObjectNode transaction1 = associate.getTransactionManager().getMapper().createObjectNode();
                    transaction1.put("timestamp", timestamp);
                    transaction1.put("description", "New card created");
                    transaction1.put("card", card.getCardNumber());
                    transaction1.put("cardHolder", email);
                    transaction1.put("account", getIBAN());
                    associate.getTransactionManager().addTransaction(transaction1);
                    return card.getCardNumber();
                } else {
                    Card card = new Card();
                    getCards().add(card);
                    ObjectNode transaction1 = associate.getTransactionManager().getMapper().createObjectNode();
                    transaction1.put("timestamp", timestamp);
                    transaction1.put("description", "New card created");
                    transaction1.put("card", card.getCardNumber());
                    transaction1.put("cardHolder", email);
                    transaction1.put("account", getIBAN());
                    associate.getTransactionManager().addTransaction(transaction1);
                    associate.getCardsCreated().add(card.getCardNumber());
                    return card.getCardNumber();
                }
            }
        }
        return null;

    }

    public void deleteCard(String email, int timestamp, String cardNumber, int cardIndex) {
        for (BusinessAssociate associate : associates) {
            if (associate.getEmail().equals(email)) {
                if (associate.getRole().equals("manager")) {
//                    ObjectNode transaction1 = associate.getTransactionManager().getMapper().createObjectNode();
//                    transaction1.put("timestamp", timestamp);
//                    transaction1.put("description", "The card has been destroyed");
//                    transaction1.put("cardHolder", users.get(i).getEmail());
//                    transaction1.put("card", users.get(i).getAccounts().get(j).getCards().get(k).getCardNumber());
//                    transaction1.put("account", users.get(i).getAccounts().get(j).getIBAN());
//                    users.get(i).getTransactionManager().addTransaction(transaction1);
                    getCards().remove(cardIndex);
                } else {
                    for (String s : associate.getCardsCreated()) {
                        if (s.equals(cardNumber)) {
                            getCards().remove(cardIndex);
                        }
                    }

                }
            }
        }

    }

    public BusinessAssociate getAssociate(String email) {
        for (BusinessAssociate associate : associates) {
            if (associate.getEmail().equals(email))
                return associate;
        }
        return null;
    }

    public ArrayList<BusinessAssociate> getAssociates() {
        return associates;
    }
}
