package org.poo.service.split;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.commands.SplitPayment;
import org.poo.service.exchange.CurrencyGraph;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;
import java.util.List;

public class SplitManager {
    private List<String> accounts;
    private List<Double> amounts;
    private int counter;
    private int accepts;
    private String type;
    private String currency;
    private int timestamp;
    ArrayList<Users> users;
    CurrencyGraph graph;
    private double total;
    public SplitManager(List<String> accounts, List<Double> amounts, String type, String currency, int timestamp, ArrayList<Users> users, CurrencyGraph graph, double total) {
        this.accounts = accounts;
        this.amounts = amounts;
        counter = 0;
        accepts = accounts.size();
        this.type = type;
        this.currency = currency;
        this.timestamp = timestamp;
        this.users = users;
        this.graph = graph;
        this.total = total;
    }
    public void addAccept(ArrayList<SplitManager> splits, String email, ArrayList<Users> users, String type, int timestamp){
        int ok = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        for (Users user : users) {
            if(user.getEmail().equals(email)){
                for (Account account : user.getAccounts()) {
                    if(account == null)
                        continue;
                    for (SplitManager split : splits) {
                        for (String splitAccount : split.getAccounts()) {
                            if(splitAccount == null)
                                continue;
                            if(account.getIBAN().equals(splitAccount) && split.getType().equals(type)){
                                ok = 1;
                                checkAccept(splits, split);
                                return;
                            }
                        }

                    }
                }

            }
        }
        if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "User not found");
            object.put("command", "acceptSplitPayment");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }
    }
    public void reject(ArrayList<SplitManager> splits, String email, ArrayList<Users> users, int timestamp) {
        Singleton singleton = Singleton.getInstance();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        ObjectNode object = singleton.getMapper().createObjectNode();
        int ok = 0;
        for (SplitManager split : splits) {
            boolean involved = false;

            for (String splitAccount : split.getAccounts()) {
                for (Users user : users) {
                    for (Account account : user.getAccounts()) {
                        if (account != null && account.getIBAN().equals(splitAccount) && user.getEmail().equals(email)) {
                            ok = 1;
                            involved = true;
                            break;
                        }
                    }
                    if (involved) break;
                }
                if (involved) break;
            }

            if (involved) {
                for (String involvedAccount : split.getAccounts()) {
                    for (Users involvedUser : users) {
                        for (Account involvedAccountObject : involvedUser.getAccounts()) {
                            if (involvedAccountObject != null && involvedAccountObject.getIBAN().equals(involvedAccount)) {
                                ObjectNode transaction = involvedUser.getTransactionManager().getMapper().createObjectNode();
                                transaction.put("timestamp", split.getTimestamp());
                                transaction.put("description", String.format("Split payment of %.2f %s", total, currency));
                                transaction.put("currency", currency);
                                transaction.put("splitPaymentType", split.getType());
                                transaction.put("error", "One user rejected the payment.");
                                ArrayNode amountForUsersNode = transaction.putArray("amountForUsers");
                                if(split.getAmounts()!=null)
                                    for (Double aDouble : split.getAmounts()) {
                                        amountForUsersNode.add(aDouble);
                                    }

                                ArrayNode involvedAccountsNode = transaction.putArray("involvedAccounts");
                                for (String iban : split.getAccounts()) {
                                    involvedAccountsNode.add(iban);
                                }

                                involvedUser.getTransactionManager().addTransaction(transaction);
                            }
                        }
                    }
                }

                splits.remove(split);
                return;
            }
        }if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "User not found");
            object.put("command", "rejectSplitPayment");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }
    }



    public void checkAccept(ArrayList<SplitManager> managers, SplitManager splitManager){
        splitManager.setCounter(splitManager.getCounter() + 1);
        if(splitManager.getCounter() == splitManager.getAccepts()){
            if(splitManager.getType().equals("custom")){
                CustomSplit customSplit = new CustomSplit(splitManager.getAccounts(), splitManager.getAmounts(), splitManager.getCurrency(), timestamp, users, graph, total);
                customSplit.execute();
                managers.remove(splitManager);
                return;
            }
            else{
                SplitPayment splitPayment = new SplitPayment(splitManager.getAccounts(), total, splitManager.getCurrency(), timestamp, users, graph);
                splitPayment.execute();
                managers.remove(splitManager);
                return;
            }
        }
    }

    public int getAccepts() {
        return accepts;
    }

    public int getCounter() {
        return counter;
    }

    public List<Double> getAmounts() {
        return amounts;

    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
