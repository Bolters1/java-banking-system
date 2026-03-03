package org.poo.service.split;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;
import java.util.List;

public class CustomSplit {
    List<String> accounts;
    List<Double> amount;
    String currency;
    int timestamp;
    ArrayList<Users> users;
    CurrencyGraph graph;
    double total;

    public CustomSplit(List<String> accounts, List<Double> amount, String currency, int timestamp, ArrayList<Users> users, CurrencyGraph graph, double total) {
        this.accounts = accounts;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.users = users;
        this.graph = graph;
        this.total = total;
    }

    public void execute() {
        ArrayList<Users> senders = new ArrayList<>();
        ArrayList<Account> sender_accounts = new ArrayList<>();
        boolean moneyCheck = true;
        String faultyAccount = null;

        for (String iban : accounts) {
            boolean accountFound = false;

            for (Users user : users) {
                for (Account account : user.getAccounts()) {
                    if (account == null) continue;

                    if (account.getIBAN().equals(iban)) {
                        accountFound = true;

                        double convertedAmount = amount.get(accounts.indexOf(iban));
                        Double exchangeRate = graph.findExchangeRate(account.getCurrency(), currency);

                        if (exchangeRate != null) {
                            convertedAmount /= exchangeRate;
                        }

                        if (account.getBalance() < convertedAmount && faultyAccount == null) {
                            faultyAccount = iban;
                            moneyCheck = false;
                        }

                        if (!senders.contains(user)) {
                            senders.add(user);
                        }

                        sender_accounts.add(account);
                    }
                }
            }

            if (!accountFound) {
                faultyAccount = iban;
                moneyCheck = false;
                break;
            }
        }

        if (!moneyCheck) {
            for (Users sender : senders) {
                ObjectNode transaction1 = sender.getTransactionManager().getMapper().createObjectNode();
                transaction1.put("timestamp", timestamp);
                transaction1.put("description", String.format("Split payment of %.2f %s", total, currency));
                transaction1.put("currency", currency);
                transaction1.put("splitPaymentType", "custom");
                transaction1.put("error", "Account " + faultyAccount + " has insufficient funds for a split payment.");

                ArrayNode amountForUsersNode = transaction1.putArray("amountForUsers");
                for (Double aDouble : amount) {
                    amountForUsersNode.add(aDouble);
                }

                ArrayNode involvedAccountsNode = transaction1.putArray("involvedAccounts");
                for (String iban : accounts) {
                    involvedAccountsNode.add(iban);
                }

                sender.getTransactionManager().addTransaction(transaction1);
            }
        } else {
            for (Users sender : senders) {
                ObjectNode transaction1 = sender.getTransactionManager().getMapper().createObjectNode();
                transaction1.put("timestamp", timestamp);
                transaction1.put("description", String.format("Split payment of %.2f %s", total, currency));
                transaction1.put("currency", currency);
                transaction1.put("splitPaymentType", "custom");

                ArrayNode amountForUsersNode = transaction1.putArray("amountForUsers");
                for (Double aDouble : amount) {
                    amountForUsersNode.add(aDouble);
                }

                ArrayNode involvedAccountsNode = transaction1.putArray("involvedAccounts");
                for (String iban : accounts) {
                    involvedAccountsNode.add(iban);
                }

                sender.getTransactionManager().addTransaction(transaction1);

                for (Account account : sender.getAccounts()) {
                    for (String iban : accounts) {
                        if (account.getIBAN().equals(iban)) {
                            Double exchangeRate = graph.findExchangeRate(account.getCurrency(), currency);
                            if (exchangeRate != null) {
                                account.setBalance(account.getBalance() - (amount.get(accounts.indexOf(iban)) / exchangeRate));
                            } else {
                                account.setBalance(account.getBalance() - amount.get(accounts.indexOf(iban)));
                            }
                        }
                    }
                }
            }
        }
    }

}
