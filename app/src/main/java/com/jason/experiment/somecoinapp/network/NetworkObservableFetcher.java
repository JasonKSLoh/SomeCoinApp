package com.jason.experiment.somecoinapp.network;

import com.google.gson.Gson;
import com.jason.experiment.somecoinapp.logging.Logg;
import com.jason.experiment.somecoinapp.model.global.GlobalResponse;
import com.jason.experiment.somecoinapp.model.listing.ListingResponse;
import com.jason.experiment.somecoinapp.model.ticker.TickerMultiResponse;
import com.jason.experiment.somecoinapp.model.ticker.TickerResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * NetworkObservableFetcher
 * Created by jason.
 */
public class NetworkObservableFetcher {


    public Observable<ListingResponse> getListingObservable() {
        String url = new ApiQuery()
                .setFunction(ApiQuery.FUNCS.LISTINGS)
                .generateUrl();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .readTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .writeTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        return Observable.fromCallable(() -> client.newCall(request).execute())
                .map(response -> {
                    if (response.body() == null) {
                        return new ListingResponse();
                    }
                    String body = response.body().string();
                    Logg.d("Response: " + body);
                    Gson gson = new Gson();
                    return gson.fromJson(body, ListingResponse.class);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Logg.e( e.getMessage(), e));
    }

    public Observable<GlobalResponse> getGlobalObservable(String currency) {
        String url = new ApiQuery()
                .setFunction(ApiQuery.FUNCS.GLOBAL_DATA)
                .setCurrency(currency)
                .generateUrl();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .readTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .writeTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        return Observable.fromCallable(() -> client.newCall(request).execute())
                .map(response -> {
                    if (response.body() == null) {
                        return new GlobalResponse();
                    }
                    String body = response.body().string();
                    Logg.d( "Response: " + body);
                    Gson gson = new Gson();
                    return gson.fromJson(body, GlobalResponse.class);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Logg.e( e.getMessage(), e));
    }

    public Observable<TickerResponse> getTickerObservable(ApiQuery apiQuery) {
        String url = apiQuery.generateUrl();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .readTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .writeTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        return Observable.fromCallable(() -> client.newCall(request).execute())
                .map(response -> {
                    if (response.body() == null) {
                        return new TickerResponse();
                    }
                    String body = response.body().string();
                    Logg.d( "Response: " + body);
                    Gson gson = new Gson();
                    return gson.fromJson(body, TickerResponse.class);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Logg.e( e.getMessage(), e));
    }

    public Observable<TickerMultiResponse> getTickerMultiObservable(ApiQuery apiQuery) {
        String url = apiQuery.generateUrl();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .readTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .writeTimeout(NetworkConsts.DEFAULT_TIMEOUT, NetworkConsts.DEFAULT_TIMEUNIT)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        return Observable.fromCallable(() -> client.newCall(request).execute())
                .map(response -> {
                    if (response.body() == null) {
                        return new TickerMultiResponse();
                    }
                    String body = response.body().string();
                    Logg.d( "Response: " + body);
                    Gson gson = new Gson();
                    return gson.fromJson(body, TickerMultiResponse.class);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Logg.e( e.getMessage(), e));
    }
}
