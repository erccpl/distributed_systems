package currency_server;

import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import static currency_server.Utils.getCurrencyRate;


public class ExchangeRateService extends ExchangeRateServiceGrpc.ExchangeRateServiceImplBase {

    private static final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());

    private List<ExchangeRateObserver> observers = new ArrayList<>();


    private List<ExchangeRate> exchangeRatesList;
    private final ReadWriteLock exchangeRatesLock;

    public ExchangeRateService(List<ExchangeRate> initialExchangeRates) {
        this.exchangeRatesList = initialExchangeRates;
        this.exchangeRatesLock = new ReentrantReadWriteLock();

        //Run the update routine in seperate thread:
        Thread currencySimulation = new Thread(this::simulateExchangeRateChanges);
        logger.info("Exchange simulation started");
        currencySimulation.start();
    }


    public void updateExchangeRates() {

        int rand1 = Utils.getRandomIndex(exchangeRatesList);
        int rand2 = Utils.getRandomIndex(exchangeRatesList);

        ExchangeRate selected1 = exchangeRatesList.get(rand1);
        ExchangeRate selected2 = exchangeRatesList.get(rand2);

        float delta1 = Utils.getRandomFloatInRange(-0.1f, 0.1f);
        float delta2 = Utils.getRandomFloatInRange(-0.1f, 0.1f);

        selected1.setRate(selected1.getRate() + delta1);
        selected2.setRate(selected2.getRate() + delta2);

        selected1.markAsChanged();
        selected2.markAsChanged();

        System.out.println("Currencies updated: " + selected1.getCurrency() + " " + selected2.getCurrency());
        System.out.println("Delta1: " + String.format("%.02f", delta1) + "\t" + "Delta2: " + String.format("%.02f", delta2));

        for (ExchangeRate e : exchangeRatesList) {
            System.out.println(e.getCurrency() + " " + String.format("%.2f", e.getRate()));
        }
    }


    public void simulateExchangeRateChanges() {

        while(true) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            exchangeRatesLock.writeLock().lock();
            updateExchangeRates();
            exchangeRatesLock.writeLock().unlock();

            for (ExchangeRateObserver o : observers){
                o.informRemoteSubscriber(this.exchangeRatesList);
            }

            exchangeRatesLock.writeLock().lock();
            resetUpdateFlag();
            exchangeRatesLock.writeLock().unlock();
        }
    }

    public void resetUpdateFlag() {
        for (ExchangeRate e: this.exchangeRatesList) {
            e.markAsUnchanged();
        }

    }

    public float convertToBaseCurrency(CurrencyRates.Currency currency, CurrencyRates.Currency baseCurrency) {
        return getCurrencyRate(exchangeRatesList, currency) / getCurrencyRate(exchangeRatesList, baseCurrency);
    }




    /***
     *
     * GRPC service
     *
     */

    @Override
    public void requestExchangeRateService (CurrencyRates.ExchangeRateServiceRequest subscription,
                          StreamObserver<CurrencyRates.RateUpdate> responseObserver){

        //Create a new ExchangeRateObserver for the bank that just called:
        ExchangeRateObserver observer = new ExchangeRateObserver(exchangeRatesList, subscription, responseObserver);
        observers.add(observer);

        //Send the initial response:
        CurrencyRates.RateUpdate.Builder initialResponse = CurrencyRates.RateUpdate.newBuilder();

        for (CurrencyRates.Currency c : subscription.getForeignCurrencyList())

        {
               CurrencyRates.CurrencyValue currencyValue = CurrencyRates.CurrencyValue
                       .newBuilder()

                       .setCurrency(c)
                       .setValue(convertToBaseCurrency(c, subscription.getBaseCurrency()))
                       .build();

               initialResponse.addExchangeRate(currencyValue);
        }

        responseObserver.onNext(initialResponse.build());
        logger.info("Sent initial response");
    }
}
