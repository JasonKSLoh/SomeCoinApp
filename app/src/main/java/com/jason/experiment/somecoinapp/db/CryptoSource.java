package com.jason.experiment.somecoinapp.db;

import com.jason.experiment.somecoinapp.model.global.GlobalDatum;
import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;

import java.util.List;

import io.reactivex.Observable;

/**
 * CryptoSource
 * Created by jason.
 */
public interface CryptoSource {

    //Tickers
    Observable<List<TickerDatum>> getAllTickers();
    Observable<TickerDatum> getTickerById(Integer id);
    Observable<TickerDatum> getTickerByName(String name);
    Observable<TickerDatum> getTickerBySymbol(String symbol);

    //Listings

    Observable<List<ListingDatum>> getAllListings();
    Observable<ListingDatum> getListingById(Integer id);
    Observable<ListingDatum> getListingByName(String name);
    Observable<ListingDatum> getListingBySymbol(String symbol);

    //Global
    Observable<GlobalDatum> getGlobalData();
}
