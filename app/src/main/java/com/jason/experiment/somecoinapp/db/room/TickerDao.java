package com.jason.experiment.somecoinapp.db.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;

import java.util.List;

/**
 * TickerDao
 * Created by jason.
 */
@Dao
public abstract class TickerDao {


    @Query("SELECT * FROM tickerdatum")
    public abstract List<TickerDatum> getAll();

    @Query("SELECT * FROM tickerdatum WHERE id == :id")
    public abstract List<TickerDatum> getById(Integer id);

    @Query("SELECT * FROM tickerdatum WHERE name == :name")
    public abstract List<TickerDatum> getByName(String name);

    @Query("SELECT * FROM tickerdatum WHERE symbol == :symbol")
    public abstract List<TickerDatum> getBySymbol(String symbol);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertTicker(TickerDatum tickerDatum);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long updateTicker(TickerDatum tickerDatum);

    @Transaction
    public long insertOrUpdateTicker(TickerDatum tickerDatum) {
        long result = insertTicker(tickerDatum);
        if (result == -1) {
            result = updateTicker(tickerDatum);
        }
        return result;
    }


    @Query("DELETE FROM TickerDatum WHERE id == :id")
    public abstract int deleteById(Integer id);

    @Query("DELETE FROM TickerDatum WHERE name == :name")
    public abstract int deleteByName(String name);

    @Query("DELETE FROM TickerDatum WHERE symbol == :symbol")
    public abstract int deleteBySymbol(String symbol);

    @Delete
    public abstract int delete(TickerDatum tickerDatum);

    @Query("DELETE FROM TickerDatum")
    public abstract int deleteAll();

}
