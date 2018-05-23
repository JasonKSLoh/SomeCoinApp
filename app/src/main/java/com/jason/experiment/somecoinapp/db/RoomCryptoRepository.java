package com.jason.experiment.somecoinapp.db;

import android.content.Context;

import com.jason.experiment.somecoinapp.db.room.CryptoDb;
import com.jason.experiment.somecoinapp.model.global.GlobalDatum;
import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.util.RxUtils;
import com.jason.experiment.somecoinapp.util.SharedPrefsUtils;

import java.util.List;

import io.reactivex.Observable;

/**
 * RoomCryptoRepository
 * Created by jason.
 */
public class RoomCryptoRepository implements CryptoRepository {

    private CryptoDb db;
    private Context  context;


    public RoomCryptoRepository(Context context) {
        this.context = context;
        db = CryptoDb.getInstance(context);
    }


    @Override
    public Observable<List<TickerDatum>> getAllTickers() {
        return RxUtils.makeIoObservable(() -> db.tickerDao().getAll());
    }

    @Override
    public Observable<TickerDatum> getTickerById(Integer id) {
        return RxUtils.makeIoObservable(() -> {
            List<TickerDatum> results = db.tickerDao().getById(id);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        });
    }

    @Override
    public Observable<TickerDatum> getTickerByName(String name) {
        return RxUtils.makeIoObservable(() -> {
            List<TickerDatum> results = db.tickerDao().getByName(name);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        });
    }

    @Override
    public Observable<TickerDatum> getTickerBySymbol(String symbol) {
        return RxUtils.makeIoObservable(() -> {
            List<TickerDatum> results = db.tickerDao().getBySymbol(symbol);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        });
    }

    @Override
    public Observable<Long> insertOrUpdateTicker(TickerDatum tickerDatum) {
        return RxUtils.makeIoObservable(() -> {
            return db.tickerDao().insertOrUpdateTicker(tickerDatum);
        });
    }

    @Override
    public Observable<Integer> deleteTickerById(Integer id) {
        return RxUtils.makeIoObservable(() -> {
            return db.tickerDao().deleteById(id);
        });
    }

    @Override
    public Observable<Integer> deleteTickerByName(String name) {
        return RxUtils.makeIoObservable(() -> {
            return db.tickerDao().deleteByName(name);
        });
    }

    @Override
    public Observable<Integer> deleteTickerBySymbol(String symbol) {
        return RxUtils.makeIoObservable(() -> {
            return db.tickerDao().deleteBySymbol(symbol);
        });
    }

    @Override
    public Observable<Integer> deleteTicker(TickerDatum tickerDatum) {
        return RxUtils.makeIoObservable(() -> {
            return db.tickerDao().delete(tickerDatum);
        });
    }

    @Override
    public Observable<Integer> deleteAllTickers() {
        return RxUtils.makeIoObservable(() -> {
            return db.tickerDao().deleteAll();
        });
    }

    @Override
    public Observable<List<ListingDatum>> getAllListings() {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().getAll();
        });
    }

    @Override
    public Observable<ListingDatum> getListingById(Integer id) {
        return RxUtils.makeIoObservable(() -> {
            List<ListingDatum> results = db.listingDao().getById(id);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        });
    }

    @Override
    public Observable<ListingDatum> getListingByName(String name) {
        return RxUtils.makeIoObservable(() -> {
            List<ListingDatum> results = db.listingDao().getByName(name);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        });
    }

    @Override
    public Observable<ListingDatum> getListingBySymbol(String symbol) {
        return RxUtils.makeIoObservable(() -> {
            List<ListingDatum> results = db.listingDao().getBySymbol(symbol);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        });
    }

    @Override
    public Observable<Long> insertOrUpdateListing(ListingDatum listingDatum) {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().insertOrUpdateListing(listingDatum);
        });
    }

    @Override
    public Observable<Integer> deleteListingById(Integer id) {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().deleteById(id);
        });
    }

    @Override
    public Observable<Integer> deleteListingByName(String name) {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().deleteByName(name);
        });
    }

    @Override
    public Observable<Integer> deleteListingBySymbol(String symbol) {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().deleteBySymbol(symbol);
        });
    }

    @Override
    public Observable<Integer> deleteListing(ListingDatum listingDatum) {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().delete(listingDatum);
        });
    }

    @Override
    public Observable<Integer> deleteAllListings() {
        return RxUtils.makeIoObservable(() -> {
            return db.listingDao().deleteAll();
        });
    }

    @Override
    public Observable<GlobalDatum> getGlobalData() {
        return RxUtils.makeIoObservable(() -> {
            return SharedPrefsUtils.getGlobalCryptoData(context);
        });
    }

    @Override
    public Observable<Integer> setGlobalData(GlobalDatum globalDatum) {
        return RxUtils.makeIoObservable(() -> {
            SharedPrefsUtils.setGlobalCryptoData(context, globalDatum);
            return 1;
        });
    }

    @Override
    public Observable<Integer> deleteGlobalData() {
        return RxUtils.makeIoObservable(() -> {
            SharedPrefsUtils.deleteGlobalCryptoData(context);
            return 1;
        });
    }
}
