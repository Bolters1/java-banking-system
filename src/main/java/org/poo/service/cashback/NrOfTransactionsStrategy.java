package org.poo.service.cashback;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

public class NrOfTransactionsStrategy implements CashbackStrategy {
    @Override
    public void applyCashback(Account account, String commerciant, double transactionAmount, double amountInRon, Users user) {
        Cashback cashback = account.getCashback();
        cashback.nrOfTransactions(commerciant);
    }
}
