package org.poo.service.users.accounts;

public class AccountFactory {
    public static Account createAccount(String email, String currency, String accountType, double... extraParams) {
        switch (accountType) {
            case "classic":
                return new ClassicAccount(email, currency, accountType);
            case "savings":
                if (extraParams.length > 0) {
                    return new SavingsAccount(email, currency, accountType, extraParams[0]);
                }
            case "business":
                return new BusinessAccount(email, currency, accountType);
            default:
                return null;
        }
    }
}
