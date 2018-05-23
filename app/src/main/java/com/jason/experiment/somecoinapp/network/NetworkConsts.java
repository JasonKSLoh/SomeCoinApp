package com.jason.experiment.somecoinapp.network;

import java.util.concurrent.TimeUnit;

/**
 * NetworkConsts
 * Created by jason.
 */
public class NetworkConsts {
    public static final String LISTING_ENDPOINT = "https://api.coinmarketcap.com/v2/listings/";
    public static final String TICKER_ENDPOINT = "https://api.coinmarketcap.com/v2/ticker/";
    public static final String GLOBAL_DATA_ENDPOINT = "https://api.coinmarketcap.com/v2/global/";

    public static final long DEFAULT_TIMEOUT = 10000L;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
}
