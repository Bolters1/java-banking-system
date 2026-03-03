package org.poo.service.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;
import org.poo.service.TransactionManager;
import org.poo.service.users.Users;
import org.poo.service.users.accounts.BusinessAccount;
import org.poo.service.users.accounts.BusinessAssociate;

import java.util.ArrayList;

public class BusinessReport implements Command {
    private int start;
    private int end;
    private String type;
    private String account;
    private ArrayList<Users> users;
    private int timestamp;

    public BusinessReport(int start, int end, String type, String account, ArrayList<Users> users, int timestamp) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.account = account;
        this.users = users;
        this.timestamp = timestamp;
    }
    @Override
    public void execute() {
        BusinessAccount businessAccount = null;
        for (Users user : users) {
            for (var acc : user.getAccounts()) {
                if (acc instanceof BusinessAccount && acc.getIBAN().equals(account)) {
                    businessAccount = (BusinessAccount) acc;
                    break;
                }
            }
            if (businessAccount != null) break;
        }

        Singleton singleton = Singleton.getInstance();
        ObjectMapper mapper = singleton.getMapper();
        ArrayNode outputArray = singleton.getOutput();

        ObjectNode report = mapper.createObjectNode();
        report.put("command", "businessReport");

        ObjectNode reportData = mapper.createObjectNode();
        reportData.put("IBAN", businessAccount.getIBAN());
        reportData.put("balance", businessAccount.getBalance());
        reportData.put("currency", businessAccount.getCurrency());
        reportData.put("spending limit", businessAccount.getSpendingLimit());
        reportData.put("deposit limit", businessAccount.getDepositLimit());

        ArrayNode managersArray = mapper.createArrayNode();
        ArrayNode employeesArray = mapper.createArrayNode();
        double totalSpent = 0;
        double totalDeposited = 0;

        for (BusinessAssociate associate : businessAccount.getAssociates()) {
            double spent = 0;
            double deposited = 0;

            TransactionManager manager = associate.getTransactionManager();
            for (var transaction : manager.getTransactions()) {
                ObjectNode transactionNode = (ObjectNode) transaction;
                int transactionTimestamp = transactionNode.get("timestamp").asInt();

                if (transactionTimestamp >= start && transactionTimestamp <= end) {
                    String description = transactionNode.get("description").asText();
                    double amount = transactionNode.get("amount").asDouble();

                    if (description.contains("payment")) {
                        spent += amount;
                    } else if (description.contains("Deposit")) {
                        deposited += amount;
                    }
                }
            }

            totalSpent += spent;
            totalDeposited += deposited;

            ObjectNode associateNode = mapper.createObjectNode();
            associateNode.put("username", associate.getName());
            associateNode.put("spent", spent);
            associateNode.put("deposited", deposited);

            if (associate.getRole().equals("manager")) {
                managersArray.add(associateNode);
            } else {
                employeesArray.add(associateNode);
            }
        }

        reportData.set("managers", managersArray);
        reportData.set("employees", employeesArray);
        reportData.put("total spent", totalSpent);
        reportData.put("total deposited", totalDeposited);
        reportData.put("statistics type", type);

        report.set("output", reportData);
        report.put("timestamp", timestamp);

        outputArray.add(report);
        singleton.setOutput(outputArray);
    }
}
