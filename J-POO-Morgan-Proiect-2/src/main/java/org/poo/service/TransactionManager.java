package org.poo.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.Singleton;

public class TransactionManager {
    private final ObjectMapper mapper;
    private final ArrayNode transactions;

    public TransactionManager() {
        this.mapper = new ObjectMapper();
        this.transactions = mapper.createArrayNode();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ArrayNode getTransactions() {
        return transactions;
    }

    public void addTransaction(ObjectNode transaction) {
        transactions.add(transaction);
    }

    public void printTransactions() {
        Singleton singleton = Singleton.getInstance();
        singleton.getOutput().add(transactions);
    }
}
