package org.poo.service.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

import java.util.ArrayList;

public class Report implements Command{
    int start;
    int end;
    int timestamp;
    String account;
    ArrayList<Users> users;
    public Report(int start, int end, int timestamp, String account, ArrayList<Users> users) {
        this.start = start;
        this.end = end;
        this.timestamp = timestamp;
        this.account = account;
        this.users = users;
    }

    @Override
    public void execute() {
        int ok = 0;
        Singleton singleton = Singleton.getInstance();
        ObjectNode object = singleton.getMapper().createObjectNode();
        ObjectNode outputNode = singleton.getMapper().createObjectNode();
        for (Users user : users) {
            for (Account userAccount : user.getAccounts()) {
                if(userAccount == null)
                    continue;
                if(userAccount.getIBAN().equals(account)){
                    ok = 1;
                    object.put("command", "report");
                    outputNode.put("balance", userAccount.getBalance());
                    outputNode.put("currency", userAccount.getCurrency());
                    outputNode.put("IBAN", userAccount.getIBAN());
                    ArrayList<JsonNode> filteredTransactions = new ArrayList<>();
                    for (JsonNode transaction : user.getTransactionManager().getTransactions()) {
                        int transactionTimestamp = transaction.get("timestamp").asInt();
                        if (transactionTimestamp <= end && transactionTimestamp >= start) {
                            filteredTransactions.add(transaction);
                        }
                    }
                    outputNode.set("transactions", singleton.getMapper().valueToTree(filteredTransactions));
                    object.set("output", outputNode);
                    object.put("timestamp", timestamp);
                    singleton.getOutput().add(object);

                }
            }

        }
        if(ok == 0){
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Account not found");
            object.put("command", "report");
            object.set("output", outputNode);
            object.put("timestamp", timestamp);

            singleton.getOutput().add(object);
        }

    }
}
