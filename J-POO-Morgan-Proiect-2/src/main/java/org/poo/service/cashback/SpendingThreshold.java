package org.poo.service.cashback;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

public class SpendingThreshold implements CashbackStrategy {

    @Override
    public void applyCashback(Account account, String commerciant, double amountInRon, double transactionAmount, Users user) {
        Cashback cashback = account.getCashback();

        cashback.spendingThreshold(amountInRon);

        String plan = user.getPlan();
        double totalSpent = cashback.getTotalSpent();

        double cashbackPercentage = 0;

        if (totalSpent >= 500) {
            if (plan.equals("gold")) {
                cashbackPercentage = 0.007;
            } else if (plan.equals("silver")) {
                cashbackPercentage = 0.005;
            } else {
                cashbackPercentage = 0.0025;
            }
        } else if (totalSpent >= 300) {
            if (plan.equals("gold")) {
                cashbackPercentage = 0.0055;
            } else if (plan.equals("silver")) {
                cashbackPercentage = 0.004;
            } else {
                cashbackPercentage = 0.002;
            }
        } else if (totalSpent >= 100) {
            if (plan.equals("gold")) {
                cashbackPercentage = 0.005;
            } else if (plan.equals("silver")) {
                cashbackPercentage = 0.003;
            } else {
                cashbackPercentage = 0.001;
            }
        }
            double cashbackAmount = transactionAmount * cashbackPercentage;
            account.setBalance(account.getBalance() + cashbackAmount);
    }
}
