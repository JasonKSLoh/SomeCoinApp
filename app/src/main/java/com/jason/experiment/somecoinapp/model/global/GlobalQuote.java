package com.jason.experiment.somecoinapp.model.global;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * GlobalQuote
 * Created by jason.
 */
public class GlobalQuote {
    @SerializedName("total_market_cap")
    @Expose
    private Double totalMarketCap;
    @SerializedName("total_volume_24h")
    @Expose
    private Double totalVolume24h;

    public Double getTotalMarketCap() {
        return totalMarketCap;
    }

    public void setTotalMarketCap(Double totalMarketCap) {
        this.totalMarketCap = totalMarketCap;
    }

    public Double getTotalVolume24h() {
        return totalVolume24h;
    }

    public void setTotalVolume24h(Double totalVolume24h) {
        this.totalVolume24h = totalVolume24h;
    }

}
