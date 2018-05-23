package com.jason.experiment.somecoinapp.db.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.jason.experiment.somecoinapp.model.listing.ListingDatum;

import java.util.List;

/**
 * ListingDao
 * Created by jason.
 */
@Dao
public abstract class ListingDao {

    @Query("SELECT * FROM listingdatum")
    public abstract List<ListingDatum> getAll();

    @Query("SELECT * FROM listingdatum WHERE id == :id")
    public abstract List<ListingDatum> getById(Integer id);

    @Query("SELECT * FROM ListingDatum WHERE name == :name")
    public abstract List<ListingDatum> getByName(String name);

    @Query("SELECT * FROM ListingDatum WHERE symbol == :symbol")
    public abstract List<ListingDatum> getBySymbol(String symbol);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertListing(ListingDatum listingDatum);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long updateListing(ListingDatum listingDatum);

    @Transaction
    public long insertOrUpdateListing(ListingDatum listingDatum){
        long result = updateListing(listingDatum);
        if(result == -1){
            result = insertListing(listingDatum);
        }
        return result;
    }

    @Query("DELETE FROM ListingDatum WHERE id == :id")
    public abstract int deleteById(Integer id);

    @Query("DELETE FROM ListingDatum WHERE name == :name")
    public abstract int deleteByName(String name);

    @Query("DELETE FROM ListingDatum WHERE symbol == :symbol")
    public abstract int deleteBySymbol(String symbol);

    @Delete
    public abstract int delete(ListingDatum listingDatum);

    @Query("DELETE FROM ListingDatum")
    public abstract int deleteAll();
}
