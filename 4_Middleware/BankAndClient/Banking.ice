module Banking
{
    enum Currency { PLN = 1, EUR, CHF, CAD, USD };

    enum AccountType { STANDARD = 1, PREMIUM };

    exception GenericException {
        string message;
    };

    exception AuthenticationFailException extends GenericException{};
    exception InvalidBalanceException extends GenericException{};
    exception PeselRegisteredException extends GenericException{};


    dictionary<Currency, double> Balance;
    struct AccountDetails {
        AccountType type;
        double declaredIncome;
        Balance balance;
    };

    interface Account {
        AccountDetails getDetails();
        void makeDeposit(Currency currency, double amount);
        void makeWithdrawal(Currency currency, double amount);
    };

    struct LoanCosts {
        double baseCurrency;
        double targetCurrency;
    };

    interface PremiumAccount extends Account {
        LoanCosts calculateLoanCosts (Currency currency, double amount, int duration);
    };

    struct RegistrationInfo {
        AccountType type;
        string password;
        Account* accountHandle;
    };

    struct AccountAccessInfo {
        AccountType type;
        Account* accountHandle;
    };

    interface Bank {
        RegistrationInfo registerNewAccount(string UID, string fullname, float declaredIncome);
        AccountAccessInfo getAccountHandle(string UID);
    };

};