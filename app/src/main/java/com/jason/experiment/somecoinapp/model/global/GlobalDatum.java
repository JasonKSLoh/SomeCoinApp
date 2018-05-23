package com.jason.experiment.somecoinapp.model.global;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * GlobalDatum
 * Created by jason.
 */
public class GlobalDatum {
    @SerializedName("active_cryptocurrencies")
    @Expose
    private Integer    activeCryptocurrencies;
    @SerializedName("active_markets")
    @Expose
    private Integer    activeMarkets;
    @SerializedName("bitcoin_percentage_of_market_cap")
    @Expose
    private Double     bitcoinPercentageOfMarketCap;
    @SerializedName("quotes")
    @Expose
    private Map<String, GlobalQuote> quotes;
    @SerializedName("last_updated")
    @Expose
    private Integer    lastUpdated;

    public Integer getActiveCryptocurrencies() {
        return activeCryptocurrencies;
    }

    public void setActiveCryptocurrencies(Integer activeCryptocurrencies) {
        this.activeCryptocurrencies = activeCryptocurrencies;
    }

    public Integer getActiveMarkets() {
        return activeMarkets;
    }

    public void setActiveMarkets(Integer activeMarkets) {
        this.activeMarkets = activeMarkets;
    }

    public Double getBitcoinPercentageOfMarketCap() {
        return bitcoinPercentageOfMarketCap;
    }

    public void setBitcoinPercentageOfMarketCap(Double bitcoinPercentageOfMarketCap) {
        this.bitcoinPercentageOfMarketCap = bitcoinPercentageOfMarketCap;
    }

    public Map<String, GlobalQuote> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, GlobalQuote> quotes) {
        this.quotes = quotes;
    }

    public Integer getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Integer lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}