package org.poo.service.users.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.poo.service.TransactionManager;
import org.poo.service.cashback.Cashback;
import org.poo.service.users.cards.Card;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class Account {
    @JsonIgnore
    private String email;
    private String IBAN;
    private double balance;
    private String currency;
    private String type;
    @JsonIgnore
    private String alias;
    ArrayList<Card> cards = new ArrayList<Card>();
    @JsonIgnore
    double minimumBalance;
    @JsonIgnore
    private TransactionManager transactionManager;
    @JsonIgnore
    Cashback cashback;

    public Account(String email, String currency, String type) {
        this.email = email;
        this.currency = currency;
        this.type = type;
        this.balance = 0;
        this.IBAN = Utils.generateIBAN();
        alias = new String();
        minimumBalance = 0;
        this.transactionManager = new TransactionManager();
        cashback = new Cashback();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public double getBalance() {
        return balance;
    }
    @JsonProperty("IBAN")
    public String getIBAN() {
        return IBAN;
    }
    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public Cashback getCashback() {
        return cashback;
    }
}
