import sys
import Ice

from src.idl_out.ice import Banking

ICE_CURRENCIES = {str(Banking.Currency.valueOf(i)): Banking.Currency.valueOf(i)
                  for i in Banking.Currency._enumerators}


class Client:

    def __init__(self, bank_address, communicator):
        self._bank_address = bank_address
        self._communicator = communicator

        bank_base = communicator.stringToProxy('Bank:' + bank_address)
        bank_proxy = Banking.BankPrx.checkedCast(bank_base)

        if bank_proxy:
            self._bank_proxy = bank_proxy
        else:
            raise RuntimeError("Invalid bank proxy")

    # ----------------------------------------------------------------------------------------------
    # User Interface
    # ----------------------------------------------------------------------------------------------

    def start(self):
        print("From here you can register or log in")
        while True:
            c = input('> ')
            if c in ['r']:
                self._register()
            if c in ['l']:
                self._login()
            if c in ['q']:
                return
            else:
                print("Unrecognized")

    def account_panel(self, account_handle, PESEL, account_type, context):
        if not self._get_account_details(account_handle, context):
            print("FAIL")

        print("Enter account operation:")

        while True:
            c = input("{} {} >".format(PESEL, account_type))

            if c in ['i']:
                self._get_account_details(account_handle, context)
            if c in ['d']:
                self._make_deposit(account_handle, context)
            if c in ['w']:
                self._make_withdrawal(account_handle, context)
            if c in ['gl']:
                self._calculate_loan_costs(account_handle, account_type, context)
            if c in ['lo']:
                print("Logging out")
                self.start()
            if c in ['q']:
                self.start()

    # -------------------------------------------------------------------------------------------
    # ice Communication Functions
    # -------------------------------------------------------------------------------------------

    def _get_account_proxy(self, PESEL, context):
        account_access_info = self._bank_proxy.getAccountHandle(PESEL, context)
        return self._cast_proxy(account_access_info.accountHandle), account_access_info.type

    def _cast_proxy(self, proxy):
        result = Banking.PremiumAccountPrx.checkedCast(proxy)
        if result is not None:
            return result

        result = Banking.AccountPrx.checkedCast(proxy)
        if result is not None:
            return result

        return proxy



    # ------------------------------------------------------------------------------------------
    # Required Functionality
    # ------------------------------------------------------------------------------------------
    def _register(self):
        name = input("Full name > ")
        PESEL = input("PESEL > ")
        monthly_income = float(input("Income > "))

        try:
            registration_info = self._bank_proxy.registerNewAccount(name, PESEL, monthly_income)
            if registration_info.accountHandle is not None:
                print("The following account was registered at the Bank:")
                print("Type: {}, Your password is: {}".format(registration_info.type, registration_info.password))
                self._account_type = registration_info.type
                self.start()
            else:
                print("An account with this PESEL already exists")
                return

        except Exception as e:
            print("error: ", str(e))

    def _login(self):
        PESEL = input("PESEL > ")
        p = input("Password > ")

        password = utils.hash_password(p)
        context = {'password': password}
        account_handle, account_type = self._get_account_proxy(PESEL, context)

        if account_handle is not None:
            self.account_panel(account_handle, PESEL, account_type, context)
        else:
            print("Invalid PESEL or password")
            self.start()

    def _get_account_details(self, account_handle, context):
        try:
            details = account_handle.getDetails(context)
            print(details.balance)
            return True
        except Exception as e:
            print('Error', str(e))
            return

    def _make_deposit(self, account_handle, context):
        try:
            currency = input("Currency > ")
            amount = float(input("Amount > "))
            currency = ICE_CURRENCIES.get(currency)

            account_handle.makeDeposit(currency, amount, context)
            self._get_account_details(account_handle, context)

        except Exception as e:
            print("error: ", str(e))

    def _make_withdrawal(self, account_handle, context):
        try:
            currency = input("Currency > ")
            amount = float(input("Amount > "))
            currency = ICE_CURRENCIES.get(currency)

            account_handle.makeWithdrawal(currency, amount, context)
            self._get_account_details(account_handle, context)
        except Exception as e:
            print("err: ", str(e))

    def _calculate_loan_costs(self, account_handle, account_type, context):
        if account_type != Banking.AccountType.PREMIUM:
            print("Your account does not support this option")
            return

        try:
            currency = ICE_CURRENCIES[input("Currency > ")]
            amount = float(input("Amount > "))
            duration = float(input("Duration > "))

            creditInfo = account_handle.calculateLoanCosts(currency, amount, duration, context)
            print("Cost in base currency: {:.2f}".format(creditInfo.baseCurrency))

            if creditInfo.targetCurrency != 0:
                print("Cost in requested currency: {:.2f}".format(creditInfo.targetCurrency))
            return

        except Exception as e:
            print('error: ', str(e))
        return





if __name__ == '__main__':
    address = sys.argv[1]
    port = sys.argv[2]

    with Ice.initialize(sys.argv) as communicator:
        bank_address = 'default -h {} -p {}'.format(address, port)
        print("Welcome to the bank")
        interface = Client(bank_address, communicator)
        interface.start()
