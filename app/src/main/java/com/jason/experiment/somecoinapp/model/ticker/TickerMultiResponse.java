package com.jason.experiment.somecoinapp.model.ticker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * TickerMultiResponse
 * Created by jason.
 */
public class TickerMultiResponse {
    @SerializedName("data")
    @Expose
    private Map<String, TickerDatum> tickerDatum;
    @SerializedName("metadata")
    @Expose
    private TickerMetadata           tickerMetadata;

    public Map<String, TickerDatum> getTickerDatum() {
        return tickerDatum;
    }

    public void setTickerDatum(Map<String, TickerDatum> tickerDatum) {
        this.tickerDatum = tickerDatum;
    }

    public TickerMetadata getTickerMetadata() {
        return tickerMetadata;
    }

    public void setTickerMetadata(TickerMetadata tickerMetadata) {
        this.tickerMetadata = tickerMetadata;
    }
}
