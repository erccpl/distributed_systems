module Banking
{
    enum Currency {
        PLN = 1,
        EUR = 2,
        CHF = 3,
        CAD = 4,
        USD = 5
    };

    //Accounts-------------------------------------------------------

    //AccountType
    enum AccountType {
        STANDARD = 1,
        PREMIUM = 2
    };

    dictionary<Currency, double> Balance;
    struct AccountDetails {
        AccountType type;
        double declaredIncome;
        Balance balance;
    };

    interface Account {
        //methods provided by this interface
        //1. getDetails 
        AccountDetails getDetails();
        //2. deposit
        void makeDeposit(Currency currency, double amount);
        //3. withdraw
        void makeWithdrawal(Currency currency, double amount);
    };

    struct LoanCosts {
        double baseCurrency;
        double targetCurrency;
    };

    interface PremiumAccount extends Account {
        //1. getLoanCost
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