package currency_server;

import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;

import static currency_server.Utils.currencyRateUpdated;
import static currency_server.Utils.getCurrencyRate;

public class ExchangeRateObserver {

    private static final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());

    private List<ExchangeRate> exchangeRatesList;


    private StreamObserver<CurrencyRates.RateUpdate> responseObserver;
    private CurrencyRates.Currency baseCurrency;
    private List<CurrencyRates.Currency> foreignCurrencyList;


    public ExchangeRateObserver(List<ExchangeRate> exchangeRatesList,
                                CurrencyRates.ExchangeRateServiceRequest subscription,
                                StreamObserver<CurrencyRates.RateUpdate> responseObserver) {

        this.exchangeRatesList = exchangeRatesList;
        this.responseObserver = responseObserver;
        this.foreignCurrencyList = subscription.getForeignCurrencyList();
        this.baseCurrency = subscription.getBaseCurrency();
    }


    public float convertToBaseCurrency(CurrencyRates.Currency currency, CurrencyRates.Currency baseCurrency) {
        return getCurrencyRate(exchangeRatesList, currency) / getCurrencyRate(exchangeRatesList, baseCurrency);
    }

    public void informRemoteSubscriber (List<ExchangeRate> ex) {
        CurrencyRates.RateUpdate.Builder response = CurrencyRates.RateUpdate.newBuilder();

        for (CurrencyRates.Currency c : foreignCurrencyList) {

            if(currencyRateUpdated(ex, c)) {

                CurrencyRates.CurrencyValue currencyValue = CurrencyRates.CurrencyValue
                        .newBuilder()

                        .setCurrency(c)
                        .setValue(convertToBaseCurrency(c, baseCurrency))
                        .build();

                response.addExchangeRate(currencyValue);
            }
        }
        logger.info("Sent an update");
        responseObserver.onNext(response.build());
    }

}
