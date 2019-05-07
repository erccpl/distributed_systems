package currency_server;

public class ExchangeRate {
    private CurrencyRates.Currency currency;
    private float rate;
    private Boolean wasUpdated;


    public ExchangeRate(CurrencyRates.Currency currency, float rate) {
        this.currency = currency;
        this.rate = rate;
        this.wasUpdated = false;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void markAsChanged() {
        this.wasUpdated = true;
    }

    public void markAsUnchanged() {
        this.wasUpdated = false;
    }

    public float getRate() {
        return this.rate;
    }

    public CurrencyRates.Currency getCurrency() {
        return this.currency;
    }

    public Boolean wasUpdated() {
        return this.wasUpdated;
    }

}
