package com.jason.experiment.somecoinapp.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NoNullEventLiveData
 * Created by jason.
 */
public class NoNullEventLiveData<T> extends MutableLiveData<T> {

    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

//    @Override
//    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
//        super.observe(owner, data -> {
//            if (data == null) {
//                return;
//            }
//            observer.onChanged(data);
//            data = null;
//        });
//    }

    @Override
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<T> observer) {
        super.observe(owner, t -> {
            if(t == null){
                return;
            }
            if (atomicBoolean.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }


    @Override
    @MainThread
    public void setValue(@Nullable T t){
        atomicBoolean.set(true);
        super.setValue(t);
    }


    @MainThread
    public void call() {
        setValue(null);
    }

}
