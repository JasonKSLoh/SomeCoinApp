package com.jason.experiment.somecoinapp.model.ticker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * TickerDatum
 * Created by jason.
 */
@Entity
public class TickerDatum {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer    id;
    @SerializedName("name")
    @Expose
    private String     name;
    @SerializedName("symbol")
    @Expose
    private String     symbol;
    @SerializedName("website_slug")
    @Expose
    private String     websiteSlug;
    @SerializedName("rank")
    @Expose
    private Integer    rank;
    @SerializedName("circulating_supply")
    @Expose
    private Double     circulatingSupply;
    @SerializedName("total_supply")
    @Expose
    private Double     totalSupply;
    @SerializedName("max_supply")
    @Expose
    private Double     maxSupply;
    @SerializedName("quotes")
    @Expose
    private Map<String, TickerQuote> quotes;
    @SerializedName("last_updated")
    @Expose
    private Long    lastUpdated;

    //region -- <Getters and Setters...>

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getWebsiteSlug() {
        return websiteSlug;
    }

    public void setWebsiteSlug(String websiteSlug) {
        this.websiteSlug = websiteSlug;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Double getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(Double circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public Double getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(Double totalSupply) {
        this.totalSupply = totalSupply;
    }

    public Double getMaxSupply() {
        return maxSupply;
    }

    public void setMaxSupply(Double maxSupply) {
        this.maxSupply = maxSupply;
    }

    public Map<String, TickerQuote> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, TickerQuote> quotes) {
        this.quotes = quotes;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    //endregion

    public static TickerDatum getEmpty(){
        TickerDatum empty = new TickerDatum();
        empty.setId(null);
        return new TickerDatum();
    }


    public static class Converter{
        @TypeConverter
        public static Map<String, TickerQuote> fromString(String value){
            Type mapType = new TypeToken<Map<String, TickerQuote>>(){}.getType();
            return new Gson().fromJson(value, mapType);
        }

        @TypeConverter
        public static String fromMap(Map<String , TickerQuote> map){
            return new Gson().toJson(map);
        }

    }

}
