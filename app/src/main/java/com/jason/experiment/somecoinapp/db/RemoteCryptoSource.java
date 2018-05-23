package com.jason.experiment.somecoinapp.db;

import com.jason.experiment.somecoinapp.model.global.GlobalDatum;
import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.model.listing.ListingResponse;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.network.ApiQuery;
import com.jason.experiment.somecoinapp.network.NetworkObservableFetcher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * RemoteCryptoSource
 * Created by jason.
 */
public class RemoteCryptoSource implements CryptoSource {

    private NetworkObservableFetcher fetcher = new NetworkObservableFetcher();

    @Override
    public Observable<List<TickerDatum>> getAllTickers() {
        ApiQuery query = new ApiQuery().setFunction(ApiQuery.FUNCS.TICKER);
        return fetcher.getTickerMultiObservable(query)
                .map(m -> {
                    List<TickerDatum> data = new ArrayList<>();
                    for (String tickerData : m.getTickerDatum().keySet()) {
                        data.add(m.getTickerDatum().get(tickerData));
                    }
                    return data;
                });
    }

    public Observable<List<TickerDatum>> getTickersByRange(int start, int numEntries) {
        ApiQuery query = new ApiQuery()
                .setFunction(ApiQuery.FUNCS.TICKER)
                .setStart(start)
                .setLimit(numEntries);
        return fetcher.getTickerMultiObservable(query)
                .map(m -> {
                    List<TickerDatum> data = new ArrayList<>();
                    for (String tickerData : m.getTickerDatum().keySet()) {
                        data.add(m.getTickerDatum().get(tickerData));
                    }
                    return data;
                });
    }

    @Override
    public Observable<TickerDatum> getTickerById(Integer id) {
        ApiQuery query = new ApiQuery().setFunction(ApiQuery.FUNCS.TICKER)
                .setId(id);
        return fetcher.getTickerObservable(query)
                .map(tickerResponse -> {
                    return tickerResponse.getData();
                });
    }

    @Override
    public Observable<TickerDatum> getTickerByName(String name) {
        return fetcher.getListingObservable()
                .map(listingResponse -> {
                    Integer id = null;
                    for (ListingDatum datum : listingResponse.getData()) {
                        if (datum.getName().equals(name)) {
                            id = datum.getId();
                            break;
                        }
                    }
                    return id;
                }).flatMap(integer -> {
                    if(integer == null){
                        return Observable.just(TickerDatum.getEmpty());
                    } else {
                        return getTickerById(integer);
                    }
                });
    }

    @Override
    public Observable<TickerDatum> getTickerBySymbol(String symbol) {
        return fetcher.getListingObservable()
                .map(listingResponse -> {
                    Integer id = null;
                    for (ListingDatum datum : listingResponse.getData()) {
                        if (datum.getSymbol().equals(symbol)) {
                            id = datum.getId();
                            break;
                        }
                    }
                    return id;
                }).flatMap(this::getTickerById);
    }

    @Override
    public Observable<List<ListingDatum>> getAllListings() {
        return fetcher.getListingObservable()
                .map(ListingResponse::getData);
    }

    @Override
    public Observable<ListingDatum> getListingById(Integer id) {
        return fetcher.getListingObservable()
                .map(ListingResponse::getData)
                .map(listingData -> {
                    if(listingData.size() > id){
                        return listingData.get(id);
                    } else {
                        return ListingDatum.getEmpty();
                    }
                });
    }

    @Override
    public Observable<ListingDatum> getListingByName(String name) {
        return fetcher.getListingObservable()
                .map(ListingResponse::getData)
                .map(listingData -> {
                    ListingDatum matchedDatum = ListingDatum.getEmpty();
                    for(ListingDatum datum: listingData){
                        if(datum.getName().equals(name)){
                            matchedDatum = datum;
                            break;
                        }
                    }
                    return matchedDatum;
                });
    }

    @Override
    public Observable<ListingDatum> getListingBySymbol(String symbol) {
        return fetcher.getListingObservable()
                .map(ListingResponse::getData)
                .map(listingData -> {
                    ListingDatum matchedDatum = ListingDatum.getEmpty();
                    for(ListingDatum datum: listingData){
                        if(datum.getSymbol().equals(symbol)){
                            matchedDatum = datum;
                            break;
                        }
                    }
                    return matchedDatum;
                });
    }

    @Override
    public Observable<GlobalDatum> getGlobalData() {
        return fetcher.getGlobalObservable(null)
                .map(globalResponse -> {
                    return globalResponse.getGlobalDatum();
                });
    }
}
