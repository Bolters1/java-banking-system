package org.poo.service.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements Command {
    List<String> accounts;
    double amount;
    String currency;
    int timestamp;
    ArrayList<Users> users;
    CurrencyGraph graph;

    public SplitPayment(List<String> accounts, double amount, String currency, int timestamp, ArrayList<Users> users, CurrencyGraph graph) {
        this.accounts = accounts;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.users = users;
        this.graph = graph;
    }

    @Override
    public void execute() {
        //System.out.println("heeey");
        ArrayList<Users> senders = new ArrayList<>();
        ArrayList<Account> sender_accounts = new ArrayList<>();
        int ok = 0;
        int moneyCheck = 1;
        String faultyAccount = new String();
        for (Users user : users) {
            ok = 0;
            for (Account account : user.getAccounts()) {
                if(account == null)
                    continue;
                for (String s : accounts) {
                    if (account.getIBAN().equals(s)) {
                        if (graph.findExchangeRate(account.getCurrency(), currency) != null) {
                            if (graph.findExchangeRate(account.getCurrency(), currency) * account.getBalance() < amount / accounts.size()) {
                                if (faultyAccount == null)
                                    faultyAccount = account.getIBAN();
                                else if (accounts.indexOf(account.getIBAN()) > accounts.indexOf(faultyAccount))
                                    faultyAccount = account.getIBAN();
                                moneyCheck = 0;
                            }
                        } else if (account.getBalance() < amount / accounts.size()) {
                            if (faultyAccount == null)
                                faultyAccount = account.getIBAN();
                            else if (accounts.indexOf(account.getIBAN()) > accounts.indexOf(faultyAccount))
                                faultyAccount = account.getIBAN();
                            moneyCheck = 0;
                        }
                        if (ok == 0)
                            senders.add(user);
                        sender_accounts.add(account);
                        ok = 1;
                    }
                }

            }

        }
        if (moneyCheck == 0) {
            for (Users sender : senders) {
                for (Account account : sender.getAccounts()) {
                    if(account == null)
                        continue;
                    for (String s : accounts) {
                        if (s.equals(account.getIBAN())) {
                            ObjectNode transaction1 = sender.getTransactionManager().getMapper().createObjectNode();
                            transaction1.put("timestamp", timestamp);
                            transaction1.put("description", String.format("Split payment of %.2f %s", amount, currency));
                            transaction1.put("error", "Account " + faultyAccount + " has insufficient funds for a split payment.");
                            transaction1.put("currency", currency);
                            transaction1.put("splitPaymentType", "equal");
                            transaction1.put("amount", amount / accounts.size());
                            ArrayNode involvedAccountsNode = transaction1.putArray("involvedAccounts");
                            for (String iban : accounts) {
                                involvedAccountsNode.add(iban);
                            }
                            sender.getTransactionManager().addTransaction(transaction1);
                        }
                    }
                }
            }
        } else {
            for (Users sender : senders) {
                ObjectNode transaction1 = sender.getTransactionManager().getMapper().createObjectNode();
                transaction1.put("timestamp", timestamp);
                transaction1.put("description", String.format("Split payment of %.2f %s", amount, currency));
                transaction1.put("currency", currency);
                transaction1.put("splitPaymentType", "equal");
                transaction1.put("amount", amount / accounts.size());
                ArrayNode involvedAccountsNode = transaction1.putArray("involvedAccounts");
                for (String iban : accounts) {
                    involvedAccountsNode.add(iban);
                }
                sender.getTransactionManager().addTransaction(transaction1);
                for (Account account : sender.getAccounts()) {
                    for (String s : accounts) {
                        if (account.getIBAN().equals(s)) {
                            if (graph.findExchangeRate(account.getCurrency(), currency) != null) {
                                account.setBalance(account.getBalance() - graph.findExchangeRate(currency, account.getCurrency()) * (amount / accounts.size()));
                            } else if (account.getBalance() < amount)
                                account.setBalance(account.getBalance() - amount / accounts.size());
                        }
                    }

                }

            }

        }
    }
}
