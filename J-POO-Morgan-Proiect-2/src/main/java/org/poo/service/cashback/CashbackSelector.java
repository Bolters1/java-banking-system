package org.poo.service.cashback;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

public class CashbackSelector {
    private CashbackStrategy strategy;

    public void setStrategy(CashbackStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeCashback(Account account, String commerciant, double transactionAmount, double amountInRon, Users user) {
        strategy.applyCashback(account, commerciant, amountInRon, transactionAmount, user);
    }
}
