package com.jason.experiment.somecoinapp.ui.search;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.db.RemoteCryptoSource;
import com.jason.experiment.somecoinapp.db.RoomCryptoRepository;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.ui.MainViewModel;

/**
 * SearchFragment
 * Created by jason.
 */
public class SearchFragment extends Fragment {
    MainViewModel     mainViewModel;
    SearchViewModel searchViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity())
                .get(MainViewModel.class);
        RemoteCryptoSource   remoteSource = new RemoteCryptoSource();
        RoomCryptoRepository localRepo    = new RoomCryptoRepository(getActivity().getApplicationContext());
        searchViewModel = ViewModelProviders.of(this, new SearchViewModel.Factory(remoteSource, localRepo)).get(SearchViewModel.class);

        bindObservers();
    }

    public void bindObservers() {
        Observer<TickerDatum> selectedCoinObserver = tickerDatum -> {
            if (tickerDatum == null) {
                showMainSearch();
            } else {
                showSearchDetail();
            }
        };
        searchViewModel.getSelectedCoin().observe(this, selectedCoinObserver);

        Observer<Boolean> backPressObserver = backPressed -> {
            if (backPressed == null
                    || mainViewModel.getCurrentNavItem().getValue() == null) {
                return;
            }
            if (mainViewModel.getCurrentNavItem().getValue() != MainViewModel.NAV_ITEM_SEARCH) {
                return;
            }
            if (searchViewModel.getSelectedCoin().getValue() != null) {
                searchViewModel.setSelectedCoin(null);
            } else {
                getActivity().finish();
            }
        };
        mainViewModel.getBackPressed().observe(getActivity(), backPressObserver);
    }

    private void showMainSearch() {
        FragmentManager     cfm = getChildFragmentManager();
        FragmentTransaction cft = cfm.beginTransaction();
        for (Fragment fragment : cfm.getFragments()) {
            cft.remove(fragment);
        }
        cft.replace(R.id.container_search, new SearchFragmentMain(), "search_main");
        cft.commitNow();
    }

    private void showSearchDetail() {
        FragmentManager     cfm = getChildFragmentManager();
        FragmentTransaction cft = cfm.beginTransaction();
        for (Fragment fragment : cfm.getFragments()) {
            cft.remove(fragment);
        }
        cft.replace(R.id.container_search, new SearchFragmentDetail(), "search_detail");
        cft.commitNow();
    }
}
