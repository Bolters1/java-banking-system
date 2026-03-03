package org.poo.service.commerciants;

public class Commerciants {
    String commerciant;
    int id;
    String account;
    String type;
    String cashbackStrategy;
    private int nrOfTransactions;

    public Commerciants(String commerciant, int id, String account, String type, String cashbackStrategy) {
        this.commerciant = commerciant;
        this.id = id;
        this.account = account;
        this.type = type;
        this.cashbackStrategy = cashbackStrategy;
    }
    public Commerciants(String commerciant){
        this.commerciant = commerciant;
        this.nrOfTransactions = 0;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public String getType() {
        return type;
    }

    public String getCashbackStrategy() {
        return cashbackStrategy;
    }

    public int getNrOfTransactions() {
        return nrOfTransactions;
    }

    public void setNrOfTransactions(int nrOfTransactions) {
        this.nrOfTransactions = nrOfTransactions;
    }

    public String getAccount() {
        return account;
    }
}
