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
import org.poo.service.users.accounts.BusinessAccount;
import org.poo.service.users.accounts.BusinessAssociate;
import org.poo.service.users.cards.Card;

import java.util.ArrayList;

public class PayOnline implements Command{
    String cardNumber;
    ArrayList<Users> users;
    double amount;
    String currency;
    String email;
    CurrencyGraph graph;
    int timestamp;
    String commerciant;
    ArrayList<Commerciants> commerciants;
    public PayOnline(String cardNumber, ArrayList<Users> users, double amount, String currency, String email, CurrencyGraph graph, int timestamp, String commerciant, ArrayList<Commerciants> commerciants) {
        this.cardNumber = cardNumber;
        this.users = users;
        this.amount = amount;
        this.currency = currency;
        this.email = email;
        this.graph = graph;
        this.timestamp = timestamp;
        this.commerciant = commerciant;
        this.commerciants = commerciants;
    }

    @Override
    public void execute() {
        int ok = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        CashbackSelector selector = new CashbackSelector();
        if(amount == 0)
            return;
        for (Users user : users) {
                for (Account account : user.getAccounts()) {
                    if(account == null)
                        continue;
                    for (Card card : account.getCards()) {
                        if(cardNumber.equals(card.getCardNumber())) {
                            if (email.equals(account.getEmail())) {
                                ok = 1;
                                if (card.getStatus().equals("frozen")) {
                                    object.put("description", "The card is frozen");
                                    object.put("timestamp", timestamp);
                                    user.getTransactionManager().addTransaction(object);
                                    return;
                                }
                                    if (account.getBalance() >= graph.findExchangeRate(currency, account.getCurrency()) * amount) {
                                        int cashback = account.getCashback().useDiscount(account.getCashback().findCommerciant(commerciants, commerciant).getType());
                                        account.setBalance(account.getBalance() - graph.findExchangeRate(currency, account.getCurrency()) * amount - user.getCommision(graph.findExchangeRate(currency, "RON") * amount) * amount * graph.findExchangeRate(currency, account.getCurrency()) + cashback * amount / 100);
//                                    account.setBalance(account.getBalance() - graph.findExchangeRate(currency, account.getCurrency()) * amount + cashback * amount / 100);
                                        if (account.getCashback().findCommerciant(commerciants, commerciant).getCashbackStrategy().equals("nrOfTransactions")) {
                                            selector.setStrategy(new NrOfTransactionsStrategy());
                                            selector.executeCashback(account, commerciant, amount, graph.findExchangeRate(currency, account.getCurrency()) * amount, null);
                                        } else {
                                            selector.setStrategy(new SpendingThreshold());
                                            selector.executeCashback(account, commerciant, graph.findExchangeRate(currency, account.getCurrency()) * amount, graph.findExchangeRate(currency, "RON") * amount, user);
                                        }
                                        if (user.getPlan().equals("silver") && amount * graph.findExchangeRate(account.getCurrency(), "RON") >= 300) {
                                            user.checkForGold();
                                        }
                                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                        transaction1.put("timestamp", timestamp);
                                        transaction1.put("description", "Card payment");
                                        transaction1.put("amount", graph.findExchangeRate(currency, account.getCurrency()) * amount);
                                        transaction1.put("commerciant", commerciant);
                                        account.getTransactionManager().addTransaction(transaction1);
                                        user.getTransactionManager().addTransaction(transaction1);
                                        //System.out.println("balance " + account.getBalance());
                                        if (card.getCardType().equals("oneTime")) {
                                            DeleteCard deleteCard = new DeleteCard(users, this.email, card.getCardNumber(), timestamp);
                                            deleteCard.execute();
                                            CreateOneTimeCard createOneTimeCard = new CreateOneTimeCard(users, account.getIBAN(), account.getEmail(), timestamp);
                                            createOneTimeCard.execute();
                                        }
                                    } else {
                                        ObjectNode transaction1 = user.getTransactionManager().getMapper().createObjectNode();
                                        transaction1.put("description", "Insufficient funds");
                                        transaction1.put("timestamp", timestamp);
                                        user.getTransactionManager().addTransaction(transaction1);
                                    }
                            }
                            else if(account instanceof BusinessAccount){
                                Users businessUser = null;
                                for (Users user1 : users) {
                                    if(user1.getEmail().equals(email))
                                        businessUser = user1;
                                }
                                ok = 1;
                                if (card.getStatus().equals("frozen")) {
                                    object.put("description", "The card is frozen");
                                    object.put("timestamp", timestamp);
                                    businessUser.getTransactionManager().addTransaction(object);
                                    return;
                                }
                                BusinessAssociate associate = ((BusinessAccount)account).getAssociate(email);
                                if(associate == null)
                                    return;
                                if(associate.getRole().equals("manager") || (associate.getRole().equals("employee") && (((BusinessAccount)account).getSpendingLimit() * graph.findExchangeRate(account.getCurrency(), currency) >= amount))) {
                                    if (account.getBalance() >= graph.findExchangeRate(currency, account.getCurrency()) * amount) {
                                        int cashback = account.getCashback().useDiscount(account.getCashback().findCommerciant(commerciants, commerciant).getType());
                                        account.setBalance(account.getBalance() - graph.findExchangeRate(currency, account.getCurrency()) * amount - user.getCommision(graph.findExchangeRate(currency, "RON") * amount) * amount * graph.findExchangeRate(currency, account.getCurrency()) + cashback * amount / 100);
//                                    account.setBalance(account.getBalance() - graph.findExchangeRate(currency, account.getCurrency()) * amount + cashback * amount / 100);
                                        if (account.getCashback().findCommerciant(commerciants, commerciant).getCashbackStrategy().equals("nrOfTransactions")) {
                                            selector.setStrategy(new NrOfTransactionsStrategy());
                                            selector.executeCashback(account, commerciant, amount, graph.findExchangeRate(currency, account.getCurrency()) * amount, null);
                                        } else {
                                            selector.setStrategy(new SpendingThreshold());
                                            selector.executeCashback(account, commerciant, graph.findExchangeRate(currency, account.getCurrency()) * amount, graph.findExchangeRate(currency, "RON") * amount, user);
                                        }
                                        if (user.getPlan().equals("silver") && amount * graph.findExchangeRate(account.getCurrency(), "RON") >= 300) {
                                            user.checkForGold();
                                        }

                                        ObjectNode transaction1 = businessUser.getTransactionManager().getMapper().createObjectNode();
                                        transaction1.put("timestamp", timestamp);
                                        transaction1.put("description", "Card payment");
                                        transaction1.put("amount", graph.findExchangeRate(currency, account.getCurrency()) * amount);
                                        transaction1.put("commerciant", commerciant);
                                        account.getTransactionManager().addTransaction(transaction1);
                                        businessUser.getTransactionManager().addTransaction(transaction1);
                                        associate.getTransactionManager().addTransaction(transaction1);
                                        associate.setTotalSpent(associate.getTotalSpent() + amount * graph.findExchangeRate(currency, account.getCurrency()));
                                        //System.out.println("balance " + account.getBalance());
                                        if (card.getCardType().equals("oneTime")) {
                                            DeleteCard deleteCard = new DeleteCard(users, this.email, card.getCardNumber(), timestamp);
                                            deleteCard.execute();
                                            CreateOneTimeCard createOneTimeCard = new CreateOneTimeCard(users, account.getIBAN(), account.getEmail(), timestamp);
                                            createOneTimeCard.execute();
                                        }
                                    } else {
                                        ObjectNode transaction1 = businessUser.getTransactionManager().getMapper().createObjectNode();
                                        transaction1.put("description", "Insufficient funds");
                                        transaction1.put("timestamp", timestamp);
                                        businessUser.getTransactionManager().addTransaction(transaction1);
                                    }
                                }
                            }
                        }
                }

            }
        }
        if (ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Card not found");
            object.put("command", "payOnline");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }

    }
}
