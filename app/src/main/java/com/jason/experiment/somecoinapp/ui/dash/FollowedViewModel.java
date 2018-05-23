package com.jason.experiment.somecoinapp.ui.dash;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.jason.experiment.somecoinapp.db.CryptoRepository;
import com.jason.experiment.somecoinapp.db.CryptoSource;
import com.jason.experiment.somecoinapp.logging.Logg;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.ui.NoNullEventLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * FollowedViewModel
 * Created by jason.
 */
public class FollowedViewModel extends ViewModel {

    private CryptoSource remoteCryptoSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CryptoRepository localCryptoRepo;

    private MutableLiveData<List<TickerDatum>> followedCoinData           = new MutableLiveData<>();
    private MutableLiveData<TickerDatum>       selectedCoin               = new MutableLiveData<>();
    private NoNullEventLiveData<Boolean>       clearFollowedButtonPressed = new NoNullEventLiveData<>();
    private NoNullEventLiveData<Boolean>       followButtonPressed        = new NoNullEventLiveData<>();

    public FollowedViewModel(CryptoSource remoteCryptoSource, CryptoRepository localCryptoRepository) {
        this.remoteCryptoSource = remoteCryptoSource;
        this.localCryptoRepo = localCryptoRepository;
        followedCoinData.setValue(new ArrayList<>());
        followButtonPressed.setValue(null);
        selectedCoin.setValue(null);
        clearFollowedButtonPressed.setValue(null);
    }

    public void onFollowButtonPressed() {
        followButtonPressed.postValue(Boolean.TRUE);
    }


    public LiveData<Boolean> getFollowButtonPressed() {
        return followButtonPressed;
    }

    public LiveData<List<TickerDatum>> getFollowedCoinData(){
        return followedCoinData;
    }

    public void setFollowedCoinData(List<TickerDatum> data){
        followedCoinData.postValue(data);
    }

    public LiveData<TickerDatum> getSelectedCoin(){
        return selectedCoin;
    }

    public void setSelectedCoin(TickerDatum datum){
        selectedCoin.postValue(datum);
    }

    public LiveData<Boolean> clearFollowedButtonPressed(){
        return clearFollowedButtonPressed;
    }

    public void onFollowedButtonPressed(){
        clearFollowedButtonPressed.postValue(Boolean.TRUE);
    }

    public void fetchFollowedCoinsLocal(ArrayList<Integer> followedCoinIds) {
        compositeDisposable.clear();
        Disposable disposable = Observable.fromIterable(followedCoinIds)
                .flatMap(id -> {
                    return localCryptoRepo.getTickerById(id);
                }).toList()
                .subscribe(data -> {
                    Collections.sort(data, (o1, o2) -> o1.getRank().compareTo(o2.getRank()));
                    followedCoinData.postValue(data);
                    for(TickerDatum datum: data){
                        Logg.d("Got Symbol: " + datum.getSymbol());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void fetchFollowedCoinsRemote(ArrayList<Integer> followedCoinIds){
        compositeDisposable.clear();
        Disposable disposable = Observable.fromIterable(followedCoinIds)
                .flatMap(id -> {
                    return remoteCryptoSource.getTickerById(id);
                }).toList()
                .subscribe(data -> {
                    Collections.sort(data, (o1, o2) -> o1.getRank().compareTo(o2.getRank()));
                    followedCoinData.postValue(data);
                    for(TickerDatum datum: data){
                        Logg.d( "Got Symbol: " + datum.getSymbol());
                    }
                });
        compositeDisposable.add(disposable);
    }

    //region -- <Factory...>
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private CryptoSource     cryptoSource;
        private CryptoRepository cryptoRepository;

        public Factory(CryptoSource cryptoSource, CryptoRepository cryptoRepository) {
            this.cryptoSource = cryptoSource;
            this.cryptoRepository = cryptoRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FollowedViewModel(cryptoSource, cryptoRepository);
        }
    }
    //endregion

}
