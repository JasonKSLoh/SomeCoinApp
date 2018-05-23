package com.jason.experiment.somecoinapp.ui.listing;

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
 * ListingViewModel
 * Created by jason.
 */
public class ListingViewModel extends ViewModel {

    private CryptoSource remoteCryptoSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CryptoRepository localCryptoRepo;

    private MutableLiveData<List<TickerDatum>> tickerData          = new MutableLiveData<>();
    private MutableLiveData<TickerDatum>       selectedCoin        = new MutableLiveData<>();
    private MutableLiveData<Integer>           currentPage         = new MutableLiveData<>();
    private NoNullEventLiveData<Boolean>       followButtonPressed = new NoNullEventLiveData<>();

    public ListingViewModel(CryptoSource remoteCryptoSource, CryptoRepository localCryptoRepository) {
        this.remoteCryptoSource = remoteCryptoSource;
        this.localCryptoRepo = localCryptoRepository;
        tickerData.setValue(new ArrayList<>());
        selectedCoin.setValue(null);
        currentPage.setValue(0);
        followButtonPressed.setValue(null);
    }

    public void fetchTickerDataRemote() {
        compositeDisposable.clear();
        Disposable disposable = remoteCryptoSource.getAllTickers()
                .flatMap(fetchedData -> {
                    Collections.sort(fetchedData, (o1, o2) -> o1.getRank().compareTo(o2.getRank()));
                    tickerData.postValue(fetchedData);
                    return Observable.fromIterable(fetchedData);
                })
                .flatMap(datum -> {
                    return localCryptoRepo.insertOrUpdateTicker(datum);
                }).toList()
                .subscribe(e -> Logg.d("Finished storing to local repo"));
        compositeDisposable.add(disposable);
    }

    public void fetchTickerDataLocal() {
        compositeDisposable.clear();
        Disposable disposable = localCryptoRepo.getAllTickers()
                .subscribe(fetchedData -> {
                    Collections.sort(fetchedData, (o1, o2) -> o1.getRank().compareTo(o2.getRank()));
                    tickerData.postValue(fetchedData);
                });
        compositeDisposable.add(disposable);
    }


    public void onFollowButtonPressed() {
        followButtonPressed.postValue(Boolean.TRUE);
    }

    //region -- <Getters and Setters...>
    public LiveData<List<TickerDatum>> getTickerData() {
        return tickerData;
    }

    public void setTickerData(List<TickerDatum> tickerData) {
        this.tickerData.postValue(tickerData);
    }

    public LiveData<TickerDatum> getSelectedCoin() {
        return selectedCoin;
    }

    public void setSelectedCoin(TickerDatum selectedCoin) {
        this.selectedCoin.postValue(selectedCoin);
    }

    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage.postValue(currentPage);
    }

    public LiveData<Boolean> getFollowButtonPressed() {
        return followButtonPressed;
    }

    //endregion

    //region -- <Factory...>
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private CryptoSource cryptoSource;
        private CryptoRepository cryptoRepository;

        public Factory(CryptoSource cryptoSource, CryptoRepository cryptoRepository) {
            this.cryptoSource = cryptoSource;
            this.cryptoRepository = cryptoRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ListingViewModel(cryptoSource, cryptoRepository);
        }
    }
    //endregion
}
