package com.jason.experiment.somecoinapp.db;

import com.jason.experiment.somecoinapp.model.global.GlobalDatum;
import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;

import io.reactivex.Observable;

/**
 * CryptoRepository
 * Created by jason.
 */
public interface CryptoRepository extends CryptoSource{

    //Tickers
//
//    List<TickerDatum> getAllTickers();
//    TickerDatum getTickerById(Integer id);
//    TickerDatum getTickerByName(String name);
//    TickerDatum getTickerBySymbol(String symbol);

    Observable<Long> insertOrUpdateTicker(TickerDatum tickerDatum);

    Observable<Integer> deleteTickerById(Integer id);
    Observable<Integer> deleteTickerByName(String name);
    Observable<Integer> deleteTickerBySymbol(String symbol);

    Observable<Integer> deleteTicker(TickerDatum tickerDatum);
    Observable<Integer> deleteAllTickers();

    //Listings

//    List<ListingDatum> getAllListings();
//    ListingDatum getListingById(Integer id);
//    ListingDatum getListingByName(String name);
//    ListingDatum getListingBySymbol(String symbol);

    Observable<Long> insertOrUpdateListing(ListingDatum listingDatum);

    Observable<Integer> deleteListingById(Integer id);
    Observable<Integer> deleteListingByName(String name);
    Observable<Integer> deleteListingBySymbol(String symbol);

    Observable<Integer> deleteListing(ListingDatum listingDatum);
    Observable<Integer> deleteAllListings();

    //Global
//    GlobalDatum getGlobalData();
    Observable<Integer> setGlobalData(GlobalDatum globalDatum);
    Observable<Integer> deleteGlobalData();

}
