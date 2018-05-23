package com.jason.experiment.somecoinapp.util;

import com.jason.experiment.somecoinapp.logging.Logg;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * RxUtils
 * Created by jason.
 */
public class RxUtils {

    public static <T>Observable<T> makeIoObservable(Callable<T> callable){
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(getDefaultErrorHandler());
    }

    public static Consumer<Throwable> getDefaultErrorHandler(){
        return e -> Logg.d(e.getMessage(), e);
    }

}
