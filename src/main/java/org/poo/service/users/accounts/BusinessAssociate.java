package org.poo.service.users.accounts;

import org.poo.service.TransactionManager;

import java.util.ArrayList;

public class BusinessAssociate {
    String email;
    String role;
    double totalSpent;
    double totalDeposited;
    String name;
    TransactionManager manager;
    ArrayList<String> cardsCreated;
    public BusinessAssociate(String email, String role, String name){
        this.name = name;
        this.email = email;
        this.role = role;
        cardsCreated = new ArrayList<>();
        manager = new TransactionManager();
    }

    public String getEmail() {
        return email;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public void setTotalDeposited(double totalDeposited) {
        this.totalDeposited = totalDeposited;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public double getTotalDeposited() {
        return totalDeposited;
    }

    public TransactionManager getTransactionManager() {
        return manager;
    }

    public ArrayList<String> getCardsCreated() {
        return cardsCreated;
    }
}
