package currency_server;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CurrencyServer {

    private static final Logger logger = Logger.getLogger(CurrencyServer.class.getName());

    /**
     * Main method.  This comment makes the linter happy.
     */

    public static void main(String[] args) throws Exception {

        List<ExchangeRate> initialExchangeRateInfo = new ArrayList<>();
        initialExchangeRateInfo.add(new ExchangeRate(CurrencyRates.Currency.PLN, 1.00f));
        initialExchangeRateInfo.add(new ExchangeRate(CurrencyRates.Currency.USD, 3.81f));
        initialExchangeRateInfo.add(new ExchangeRate(CurrencyRates.Currency.CHF, 3.75f));
        initialExchangeRateInfo.add(new ExchangeRate(CurrencyRates.Currency.EUR, 4.27f));
        initialExchangeRateInfo.add(new ExchangeRate(CurrencyRates.Currency.CAD, 4.94f));

        Server server = NettyServerBuilder.forAddress(new InetSocketAddress("127.0.0.1", 60001))
                .addService(new ExchangeRateService(initialExchangeRateInfo))
                .build();

        server.start();
        logger.info("Server started");
        server.awaitTermination();
    }
}
