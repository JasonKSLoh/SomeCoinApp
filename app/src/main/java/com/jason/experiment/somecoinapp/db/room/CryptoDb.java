package com.jason.experiment.somecoinapp.db.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;

/**
 * CryptoDb
 * Created by jason.
 */
@Database(entities = {TickerDatum.class, ListingDatum.class}, version = 1, exportSchema = false)
@TypeConverters({TickerDatum.Converter.class})
public abstract class CryptoDb extends RoomDatabase {

    private static CryptoDb instance;

    public abstract ListingDao listingDao();
    public abstract TickerDao tickerDao();

    public static CryptoDb getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context, CryptoDb.class, "default-database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public static void destroyInstance(){
        instance = null;
    }


}
