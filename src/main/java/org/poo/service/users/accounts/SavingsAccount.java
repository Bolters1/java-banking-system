package org.poo.service.users.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.poo.service.users.accounts.Account;

public class SavingsAccount extends Account {
    @JsonIgnore
    private double interestRate;
    @JsonIgnore

    public SavingsAccount(String email, String currency, String accountType, double interestRate){
        super(email, currency, accountType);
        this.interestRate = interestRate;
    }
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }
}
