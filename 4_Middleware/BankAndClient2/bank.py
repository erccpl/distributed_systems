import sys
import Ice
import json
import threading
from threading import Lock
import grpc
import logging
logging.basicConfig(level=logging.DEBUG)

import hashlib
import binascii
import random


import Banking

import currency_rates_pb2_grpc
import currency_rates_pb2



ICE_CURRENCIES_TO_STRING = {Banking.Currency.valueOf(i) : str(Banking.Currency.valueOf(i))
                            for i in Banking.Currency._enumerators}

STRING_TO_ICE_CURRENCIES = {str(Banking.Currency.valueOf(i)) : Banking.Currency.valueOf(i)
                            for i in Banking.Currency._enumerators}


def verify_password(stored_password, provided_password):
    """Verify a stored password against one provided by user"""
    salt = stored_password[:64]
    stored_password = stored_password[64:]
    pwdhash = hashlib.pbkdf2_hmac('sha512',
                                  provided_password.encode('utf-8'),
                                  salt.encode('ascii'),
                                  100000)
    pwdhash = binascii.hexlify(pwdhash).decode('ascii')
    return pwdhash == stored_password





class AccountI (Banking.Account):
    '''
    This is a regular account
    Can do everthing except handle loan requests
    -getAccountDetails
    -makeDeposit
    -makeWithdrawal
    '''

    def __init__(self, full_name, PESEL, password,
                 monthly_income, account_type,
                 base_currency, foreign_currencies,
                 exchange_rates):

        self._full_name = full_name
        self._PESEL = PESEL
        self._password = password
        self._monthly_income = monthly_income

        self._account_type = account_type
        self._base_currency = base_currency
        self._foreign_currencies = foreign_currencies
        self._exchange_rates = exchange_rates

        curs = foreign_currencies + [base_currency]
        self._balance = {c.upper(): 0.0 for c in curs}
        print(self._balance)


    '''
    Slice methods:
    '''
    def getDetails(self, current):
        auth = bank.authenticate(self._PESEL, current)
        if auth:

            balance = {
                STRING_TO_ICE_CURRENCIES[key] : value
                for key, value in self._balance.items()
            }

            return Banking.AccountDetails(
                type=self._account_type,
                declaredIncome = self._monthly_income,
                balance = balance
            )
        else:
            return Banking.AccountDetails(type=None)



    def makeDeposit(self, currency, amount, current=None):
        auth = bank.authenticate(self._PESEL, current)
        if auth:
            try:
                cur = ICE_CURRENCIES_TO_STRING[currency]
                self._balance[cur] += amount

            except KeyError:
                logging.error("This currency is not supported")



    def makeWithdrawal(self, currency, amount, current=None):
        auth = bank.authenticate(self._PESEL, current)
        if auth:
            try:
                cur = ICE_CURRENCIES_TO_STRING[currency]
                if self._balance[cur] - amount > 0:
                    self._balance[cur] -= amount
                else:
                    logging.error("Not enough minerals")
            except Exception as e:
                logging.error(str(e))



class PremiumAccountI(AccountI, Banking.PremiumAccount):
    '''
    This is the premium account
    It can do all that standard does + handle load requests
    -getLoanCost
    '''


    '''
    Slice methods:
    '''
    def calculateLoanCosts(self, currency, amount, duration, current=None):
        auth = bank.authenticate(self._PESEL, current)
        if auth:
            try:
                currency_string = ICE_CURRENCIES_TO_STRING[currency]

                print("Calculating costs for a {} {} loan with a duration of {} months"
                .format(amount, currency, duration))

                bank_fee = amount * duration * 0.02
                total = amount + bank_fee

                if currency_string == self._base_currency:
                    return Banking.LoanCosts(baseCurrency = total)

                rates = self._exchange_rates()
                cost_in_base_currency = (rates[currency_string] + 0.05) * (total)

                return Banking.LoanCosts(baseCurrency=cost_in_base_currency,
                                         targetCurrency=total)
            except KeyError:
                logging.error("This currency is not supported")

        else:
            return Banking.LoanCosts(baseCurrency=None)


class BankI(Banking.Bank):
    '''
    This class represents the bank.
    Crucially it has a list of all the accounts

    The bank can:
    -create and account,
    -give you a handle to your account

    -Also: connects to the CurrencyServer
    '''

    def __init__(self, config, communicator, adapter):
        self._config = config
        self._base_currency = config['baseCurrency']
        self._foreign_currencies = config['foreignCurrencies']

        self._premium_account_threshold = config['premiumAccountThreshold']

        self._communicator = communicator
        self._adapter = adapter

        self._exchange_rates = {}

        self._accounts = {}

        self._rates_lock = Lock()



    def authenticate(self, PESEL, current):
        try:
            p = current.ctx.get("password")
            password = verify_password(p, self._accounts[PESEL]._password)

            if password == True:
                return True
            else:
                logging.critical("Authentication failed")
                return False
        except KeyError:
            logging.error("Auth: no such PESEL found")


    def _get_exchange_rates(self):
        with self._rates_lock:
            return self._exchange_rates.copy()


    def _get_account_proxy(self, PESEL, current):
        try:
            account = self._accounts[PESEL]
            account_type = account._account_type
            identifier = PESEL + str(account_type)

            base = current.adapter.createProxy(Ice.stringToIdentity(identifier))

            if account_type == Banking.AccountType.STANDARD:
                return Banking.AccountPrx.uncheckedCast(base)

            else:
                return Banking.PremiumAccountPrx.uncheckedCast(base)

        except KeyError:
            logging.error("No such account")


    '''
    Slice methods:
    '''
    def registerNewAccount(self, fullName, PESEL, declaredMonthlyIncome, current=None):
        if PESEL in self._accounts.keys():
            logging.info("This person already has an account at our bank")
            return Banking.RegistrationInfo(accountHandle=None)

        new_password = str(random.randint(999, 9999))

        if declaredMonthlyIncome < self._premium_account_threshold:
            ac_type = Banking.AccountType.STANDARD
            account = AccountI(fullName, PESEL, new_password,
                                      declaredMonthlyIncome, ac_type,
                                      self._base_currency,
                                      self._foreign_currencies,
                                      self._get_exchange_rates)

        else:
            ac_type = Banking.AccountType.PREMIUM
            account = PremiumAccountI(fullName, PESEL,
                                             new_password,
                                             declaredMonthlyIncome,
                                             ac_type,
                                             self._base_currency,
                                             self._foreign_currencies,
                                             self._get_exchange_rates)


        identifier = PESEL + str(ac_type)


        self._adapter.add(account, communicator.stringToIdentity(identifier))
        self._accounts[PESEL] = account

        logging.info('New account created: (PESEL = {}, type = {}, password = {})'.format(PESEL, str(ac_type), new_password))

        return Banking.RegistrationInfo(ac_type, new_password, self._get_account_proxy(PESEL, current))


    def getAccountHandle(self, PESEL, current=None):
        auth = self.authenticate(PESEL, current)
        if auth:
            try:
                account_handle = Banking.AccountAccessInfo(
                    self._accounts[PESEL]._account_type,
                    self._get_account_proxy(PESEL, current)
                )
                return account_handle

            except KeyError as e:
                logging.error("No such PESEL")
        else:
            logging.error("Invalid PESEL or password")
            return Banking.AccountAccessInfo(accountHandle=None)


# ================================================================================
# DEALING WITH CURRENCY SERVER
# ================================================================================


    def _connect_to_exchange_rates_provider(self):

        def update():
            rates_server_address = '{}:{}'.format(
                self._config['ratesProviderAddress'],
                self._config['ratesProviderPort'])

            with grpc.insecure_channel(rates_server_address) as channel:

                stub = currency_rates_pb2_grpc.ExchangeRateServiceStub(channel)

                msg = currency_rates_pb2.Subscription()
                msg.homeCurrency = currency_rates_pb2.Currency.Value(self._base_currency)

                f_cur = [currency_rates_pb2.Currency.Value(c)
                         for c in self._foreign_currencies]
                msg.foreignCurrencies[:] = f_cur

                updates = stub.subscribe(msg)

                try:
                    for u in updates:
                        self._update_exchange_rates(u)
                except grpc._channel._Rendezvous as err:
                    logging.error(err)

        update_thread = threading.Thread(target=update)
        update_thread.start()



    def _update_exchange_rates(self, update):
        with self._rates_lock:
            for r in update.rates:
                cur = currency_rates_pb2.Currency.Name(r.currency)
                self._exchange_rates[cur] = r.value
                print("{} {:.2f}".format( cur, self._exchange_rates[cur]))




if __name__ == '__main__':


    json_path = sys.argv[1]
    json_object = sys.argv[2]
    global_config = json.load(open(json_path))
    local_config = global_config[json_object]

    with Ice.initialize(sys.argv) as communicator:
        address = local_config['address']
        port = local_config['port']

        # Make the server listen on given address:port
        adapter = communicator.createObjectAdapterWithEndpoints("BankAdapter", "default -p {} ".format(port))

        # We can now create a servant for the Bank interface by instantiating BankI:
        bank = BankI(local_config, communicator, adapter)

        # Inform object adapted of presence of new servant, we also give it a name
        adapter.add(bank, communicator.stringToIdentity("Bank"))

        # Activate the adapter
        adapter.activate()

        # Connect to the currency service
        bank._connect_to_exchange_rates_provider()

        print(bank._exchange_rates)

        # Suspends calling thread until server implementation terminates
        communicator.waitForShutdown()
