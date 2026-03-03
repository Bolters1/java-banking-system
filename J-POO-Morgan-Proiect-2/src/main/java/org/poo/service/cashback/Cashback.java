package org.poo.service.cashback;

import org.poo.service.commerciants.Commerciants;
import org.poo.service.exchange.CurrencyGraph;

import java.util.ArrayList;

public class Cashback {
    private double totalSpent;
    private ArrayList<Commerciants> commerciants;
    private int foodDiscounts = 0;
    private int clothesDiscounts = 0;
    private int techDiscounts = 0;
    CurrencyGraph graph;

    public Cashback() {
        this.commerciants = new ArrayList<>();
        this.totalSpent = 0;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void nrOfTransactions(String commerciantName) {

        Commerciants commerciant = findOrCreateCommerciant(commerciantName);

        commerciant.setNrOfTransactions(commerciant.getNrOfTransactions() + 1);

        checkAndAddDiscount(commerciant);
    }

    public void spendingThreshold(double amount){
            totalSpent = amount + totalSpent;
    }

    private Commerciants findOrCreateCommerciant(String commerciantName) {
        for (Commerciants commerciant : commerciants) {
            if (commerciant.getCommerciant().equals(commerciantName)) {
                return commerciant;
            }
        }

        Commerciants newCommerciant = new Commerciants(commerciantName);
        commerciants.add(newCommerciant);
        return newCommerciant;
    }

    private void checkAndAddDiscount(Commerciants commerciant) {
        int transactions = commerciant.getNrOfTransactions();

        if (transactions == 2) {
            foodDiscounts++;
        } else if (transactions == 5) {
            clothesDiscounts++;
        } else if (transactions == 10) {
            techDiscounts++;
        }
    }

    public int getFoodDiscounts() {
        return foodDiscounts;
    }

    public int getClothesDiscounts() {
        return clothesDiscounts;
    }

    public int getTechDiscounts() {
        return techDiscounts;
    }

    public int useDiscount(String type) {
        if (type.equals("Food") && foodDiscounts > 0) {
            foodDiscounts--;
            return 2;
        } else if (type.equals("Clothes") && clothesDiscounts > 0) {
            clothesDiscounts--;
            return 5;
        } else if (type.equals("Tech") && techDiscounts > 0) {
            techDiscounts--;
            return 10;
        } else {
            return 0;
        }
    }
    public Commerciants findCommerciant(ArrayList<Commerciants> commerciants, String commerciant){
        for (Commerciants commerciantList : commerciants) {
            if(commerciantList.getCommerciant().equals(commerciant)){
                return commerciantList;
            }
        }
        return null;
    }
}
