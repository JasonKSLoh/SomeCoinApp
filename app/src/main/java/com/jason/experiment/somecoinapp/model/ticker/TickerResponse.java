package com.jason.experiment.somecoinapp.model.ticker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * TickerResponse
 * Created by jason.
 */
public class TickerResponse {
    @SerializedName("data")
    @Expose
    private TickerDatum tickerDatum;
    @SerializedName("metadata")
    @Expose
    private TickerMetadata tickerMetadata;

    public TickerDatum getData() {
        return tickerDatum;
    }

    public void setData(TickerDatum tickerDatum) {
        this.tickerDatum = tickerDatum;
    }

    public TickerMetadata getMetadata() {
        return tickerMetadata;
    }

    public void setMetadata(TickerMetadata metadata) {
        this.tickerMetadata = metadata;
    }

}
