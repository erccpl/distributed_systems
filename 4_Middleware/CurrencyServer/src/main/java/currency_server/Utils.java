package currency_server;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Utils {

    static float getRandomFloatInRange(float min, float max) {
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    public static int getRandomIndex(List<ExchangeRate> list)
    {
        Random rand = new Random();
        return rand.nextInt(list.size());
    }

    public static float getCurrencyRate(List<ExchangeRate> list, CurrencyRates.Currency currency) {
        Optional<ExchangeRate> matchingObject = list.stream().
                filter(p -> p.getCurrency()==currency).
                findFirst();
        return matchingObject.get().getRate();
    }

    public static Boolean currencyRateUpdated(List<ExchangeRate> list, CurrencyRates.Currency currency) {
        Optional<ExchangeRate> matchingObject =
                list.stream().
                        filter(p -> p.wasUpdated() && p.getCurrency()==currency).
                        findFirst();

        System.out.println(matchingObject.isPresent());
        return matchingObject.isPresent();
    }
}
