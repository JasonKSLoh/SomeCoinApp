package com.jason.experiment.somecoinapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.jason.experiment.somecoinapp.logging.Logg;
import com.jason.experiment.somecoinapp.model.global.GlobalDatum;

import java.util.ArrayList;

/**
 * SharedPrefsUtils
 * Created by jason.
 */
public class SharedPrefsUtils {

    private static final String LISTING_LAST_UPDATED = "listing_last_updated";
    private static final String GLOBAL_CRYPTO_DATA   = "global_crypto_data";
    private static final String SAVED_COINS          = "saved_coins";

    public static long getListingLastUpdated(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(LISTING_LAST_UPDATED, 0L);
    }

    public static void setListingLastUpdated(Context context, long lastUpdated) {
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor            = sharedPreferences.edit();
        editor.putLong(LISTING_LAST_UPDATED, lastUpdated);
        editor.apply();
    }

    public static void setGlobalCryptoData(Context context, GlobalDatum globalDatum) {
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor            = sharedPreferences.edit();
        String                   cryptoDataAsJson  = new Gson().toJson(globalDatum);
        editor.putString(GLOBAL_CRYPTO_DATA, cryptoDataAsJson);
        editor.apply();
    }

    public static GlobalDatum getGlobalCryptoData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String            cryptoDataAsJson  = sharedPreferences.getString(GLOBAL_CRYPTO_DATA, null);
        if (cryptoDataAsJson == null) {
            return null;
        }
        return new Gson().fromJson(cryptoDataAsJson, GlobalDatum.class);
    }

    public static void deleteGlobalCryptoData(Context context) {
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor            = sharedPreferences.edit();
        editor.remove(GLOBAL_CRYPTO_DATA);
        editor.apply();
    }

    public static boolean isCoinSaved(Context context, int id){
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String idString = String.valueOf(id);

        String savedCoinIds = sharedPreferences.getString(SAVED_COINS, null);
        ArrayList<String> savedCoinsArray = MiscUtils.separatedStringToArrayList(savedCoinIds, ",");
        if(savedCoinsArray == null){
            return false;
        }
        return savedCoinsArray.contains(idString);
    }

    public static void unsaveCoin(Context context, int id) {
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor            = sharedPreferences.edit();

        String idString = String.valueOf(id);

        String savedCoinIds = sharedPreferences.getString(SAVED_COINS, null);
        ArrayList<String> savedCoinsArray = MiscUtils.separatedStringToArrayList(savedCoinIds, ",");
        if(savedCoinsArray == null){
            return;
        }
        if(!savedCoinsArray.contains(idString)){
            return;
        }
        savedCoinsArray.remove(idString);
        savedCoinIds = MiscUtils.ArrayListToSeparatedString(savedCoinsArray, ",");

        editor.putString(SAVED_COINS, savedCoinIds);
        editor.apply();
    }

    public static void saveCoin(Context context, int id) {
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor            = sharedPreferences.edit();

        String idString = String.valueOf(id);

        String savedCoinIds = sharedPreferences.getString(SAVED_COINS, null);
        ArrayList<String> savedCoinsArray = MiscUtils.separatedStringToArrayList(savedCoinIds, ",");
        if(savedCoinsArray == null){
            savedCoinsArray = new ArrayList<>();
        }
        if(savedCoinsArray.contains(idString)){
            return;
        }
        savedCoinsArray.add(idString);
        savedCoinIds = MiscUtils.ArrayListToSeparatedString(savedCoinsArray, ",");

        editor.putString(SAVED_COINS, savedCoinIds);
        editor.apply();
    }

    public static void unsaveAllCoins(Context context) {
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor            = sharedPreferences.edit();
        editor.remove(SAVED_COINS);
        editor.apply();
    }

    public static ArrayList<Integer> getSavedCoins(Context context){
        SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String savedCoinIds = sharedPreferences.getString(SAVED_COINS, null);
        ArrayList<String> savedCoinsArray = MiscUtils.separatedStringToArrayList(savedCoinIds, ",");
        if(savedCoinsArray == null){
            savedCoinsArray = new ArrayList<>();
        }
        ArrayList<Integer> intArray = new ArrayList<>();
        for(String s: savedCoinsArray){
            try{
                intArray.add(Integer.parseInt(s));
            } catch (NumberFormatException nfe){
                Logg.d("Error parsing saved coins IDs");
            }
        }
        return intArray;
    }


}
