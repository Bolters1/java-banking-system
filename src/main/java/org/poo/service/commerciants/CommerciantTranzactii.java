package org.poo.service.commerciants;

public class CommerciantTranzactii {
    String commerciant;
    double total = 0;
    public CommerciantTranzactii(String name, double amount){
        this.commerciant = name;
        total = amount;
    }

    public double getTotal() {
        return total;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public void setCommerciant(String commerciant) {
        this.commerciant = commerciant;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
