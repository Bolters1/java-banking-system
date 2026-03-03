package org.poo.service.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.commerciants.CommerciantTranzactii;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;

public class SpendingsReport implements Command{
    int start;
    int end;
    int timestamp;
    String account;
    ArrayList<Users> users;

    public SpendingsReport(int start, int end, int timestamp, String account, ArrayList<Users> users) {
        this.start = start;
        this.end = end;
        this.timestamp = timestamp;
        this.account = account;
        this.users = users;
    }

    @Override
    public void execute() {
        int account_found = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode object = singleton.getMapper().createObjectNode();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if (userAccount.getIBAN().equals(account)) {
                    account_found = 1;
                    object.put("command", "spendingsReport");
                    if(userAccount.getType().equals("savings")){
                        outputNode.put("error", "This kind of report is not supported for a saving account");
                        object.set("output", outputNode);
                        object.put("timestamp", timestamp);
                        singleton.getOutput().add(object);
                        return;
                    }
                    ArrayList<JsonNode> filteredTransactions = new ArrayList<>();
                    ArrayList<CommerciantTranzactii> commerciantTranzactiis = new ArrayList<>();

                    for (JsonNode transaction : userAccount.getTransactionManager().getTransactions()) {
                        int transactionTimestamp = transaction.get("timestamp").asInt();
                        int ok = 0;
                        if (transactionTimestamp <= end && transactionTimestamp >= start && transaction.has("commerciant")) {
                            for (CommerciantTranzactii commerciantTranzactii : commerciantTranzactiis) {
                                if (commerciantTranzactii.getCommerciant().equals(transaction.get("commerciant").asText())) {
                                    commerciantTranzactii.setTotal(commerciantTranzactii.getTotal() + transaction.get("amount").asDouble());
                                    ok = 1;
                                }
                            }
                            if (ok == 0) {
                                commerciantTranzactiis.add(new CommerciantTranzactii(transaction.get("commerciant").asText(), transaction.get("amount").asDouble()));
                            }
                            filteredTransactions.add(transaction);
                        }
                    }
                    commerciantTranzactiis.sort((a, b) -> a.getCommerciant().compareTo(b.getCommerciant()));
                    outputNode.put("balance", userAccount.getBalance());
                    outputNode.put("currency", userAccount.getCurrency());
                    outputNode.put("IBAN", userAccount.getIBAN());
                    outputNode.set("commerciants", singleton.getMapper().valueToTree(commerciantTranzactiis));
                    outputNode.set("transactions", singleton.getMapper().valueToTree(filteredTransactions));

                    object.set("output", outputNode);
                    object.put("timestamp", timestamp);

                    singleton.getOutput().add(object);
                }
            }
        }
        if (account_found == 0){
                outputNode.put("timestamp", timestamp);
                outputNode.put("description", "Account not found");
                object.put("command", "spendingsReport");
                object.set("output", outputNode);
                object.put("timestamp", timestamp);

                singleton.getOutput().add(object);
        }
    }
}

