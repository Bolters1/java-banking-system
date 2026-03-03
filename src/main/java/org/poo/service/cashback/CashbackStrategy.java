package org.poo.service.cashback;

import org.poo.service.users.Users;
import org.poo.service.users.accounts.Account;

public interface CashbackStrategy {
    void applyCashback(Account account, String commerciant, double transactionAmount, double amountInRon, Users user);
}
