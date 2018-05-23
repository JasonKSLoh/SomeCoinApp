package com.jason.experiment.somecoinapp.ui.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.jason.experiment.somecoinapp.db.CryptoRepository;
import com.jason.experiment.somecoinapp.db.CryptoSource;
import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.network.ApiQuery;
import com.jason.experiment.somecoinapp.ui.NoNullEventLiveData;
import com.jason.experiment.somecoinapp.logging.Logg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * SearchViewModel
 * Created by jason.
 */
public class SearchViewModel extends ViewModel {

    public static final Long RECENCY_THRESHOLD = 24 * 60 * 60 * 1000L;
//    public static final Long RECENCY_THRESHOLD = 1L;

    private CryptoSource remoteCryptoSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CryptoRepository localCryptoRepo;

    private MutableLiveData<List<ListingDatum>> listingData         = new MutableLiveData<>();
    private MutableLiveData<List<ListingDatum>> searchedData        = new MutableLiveData<>();
    private MutableLiveData<TickerDatum>        selectedCoin        = new MutableLiveData<>();
    private NoNullEventLiveData<Boolean>        searchButtonPressed = new NoNullEventLiveData<>();
    private NoNullEventLiveData<Boolean>        followButtonPressed = new NoNullEventLiveData<>();

    public SearchViewModel(CryptoSource remoteCryptoSource, CryptoRepository localCryptoRepository) {
        this.remoteCryptoSource = remoteCryptoSource;
        this.localCryptoRepo = localCryptoRepository;
        listingData.setValue(new ArrayList<>());
        searchedData.setValue(new ArrayList<>());
        selectedCoin.setValue(null);
        searchButtonPressed.setValue(null);
    }


    public void onFollowButtonPressed() {
        followButtonPressed.postValue(Boolean.TRUE);
    }


    public LiveData<Boolean> getFollowButtonPressed() {
        return followButtonPressed;
    }

    public void fetchLocalListing() {
        compositeDisposable.clear();
        Disposable disposable = localCryptoRepo.getAllListings()
                .subscribe(data -> {
                    Collections.sort(data, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                    listingData.postValue(data);
                    Logg.d("Got local listing data");
                });
        compositeDisposable.add(disposable);
    }

    public void fetchRemoteListing() {
        compositeDisposable.clear();
//        Disposable disposable = remoteCryptoSource.getAllListings()
//                .subscribe(data -> {
//                    Collections.sort(data, (o1, o2) -> o1.getId().compareTo(o2.getId()));
//                    listingData.postValue(data);
//                    Logg.d( "Got remote listing data");
//                });

        Disposable disposable = remoteCryptoSource.getAllListings()
                .flatMap(data -> {
                    Collections.sort(data, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                    listingData.postValue(data);
                    return Observable.fromIterable(data);
                })
                .flatMap(datum -> {
                    return localCryptoRepo.insertOrUpdateListing(datum);
                }).toList()
                .subscribe(e -> Logg.d( "Finished storing listing to local repo"));

        compositeDisposable.add(disposable);
    }

    public void findMatchingListings(String input){
        Logg.d( "In find matching listings");
        List<ListingDatum> data = listingData.getValue();
        if(data == null || data.isEmpty()){

            Logg.d( "Is null or empty");
            return;
        }
        String regexInput = ".*" + input.toLowerCase() + ".*";
        List<ListingDatum> matchingData = new ArrayList<>();
        for(ListingDatum datum: data){
            String id = datum.getId().toString();
            String name = datum.getName();
            String symbol = datum.getSymbol();

            if(id.toLowerCase().matches(input)
                    || name.toLowerCase().matches(regexInput)
                    || symbol.toLowerCase().matches(regexInput)){
                matchingData.add(datum);
            }
        }

        Logg.d( "Found matching items: " + matchingData.size());
        searchedData.postValue(matchingData);
    }

    public void fetchSelectedCoinRemote(int id){
        compositeDisposable.clear();
        ApiQuery query = new ApiQuery().setId(id).setFunction(ApiQuery.FUNCS.TICKER);
        Disposable disposable = remoteCryptoSource.getTickerById(id)
                .subscribe(tickerDatum -> {
                   setSelectedCoin(tickerDatum);
                });
        compositeDisposable.add(disposable);
    }

    public void fetchSelectedCoinLocal(int id){
        compositeDisposable.clear();
        ApiQuery query = new ApiQuery().setId(id).setFunction(ApiQuery.FUNCS.TICKER);
        Disposable disposable = localCryptoRepo.getTickerById(id)
                .subscribe(tickerDatum -> {
                    setSelectedCoin(tickerDatum);
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<TickerDatum> getSelectedCoin() {
        return selectedCoin;
    }

    public void setSelectedCoin(TickerDatum datum) {
        selectedCoin.postValue(datum);
    }

    public LiveData<List<ListingDatum>> getListingData() {
        return listingData;
    }

    public void setListingData(List<ListingDatum> listingData) {
        this.listingData.postValue(listingData);
    }

    public LiveData<List<ListingDatum>> getSearchedData() {
        return searchedData;
    }

    public void setSearchedData(List<ListingDatum> searchedData) {
        this.searchedData.postValue(searchedData);
    }

    public LiveData<Boolean> getSearchButtonPressed() {
        return searchButtonPressed;
    }

    public void onSearchButtonPressed() {
        searchButtonPressed.postValue(Boolean.TRUE);
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
            return (T) new SearchViewModel(cryptoSource, cryptoRepository);
        }
    }
    //endregion

}
