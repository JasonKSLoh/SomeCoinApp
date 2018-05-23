package com.jason.experiment.somecoinapp.ui.listing;

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
 * HomeFragment
 * Created by jason.
 */
public class ListingFragment extends Fragment {

    MainViewModel    mainViewModel;
    ListingViewModel listingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity())
                .get(MainViewModel.class);
        RemoteCryptoSource remoteSource = new RemoteCryptoSource();
        RoomCryptoRepository localRepo = new RoomCryptoRepository(getActivity().getApplicationContext());
        listingViewModel = ViewModelProviders.of(this, new ListingViewModel.Factory(remoteSource, localRepo)).get(ListingViewModel.class);

        bindObservers();
    }

    private void bindObservers() {
        Observer<TickerDatum> selectedCoinObserver = tickerDatum -> {
            if (tickerDatum == null) {
                showGeneralListing();
            } else {
                showSpecificListing();
            }
        };
        listingViewModel.getSelectedCoin().observe(this, selectedCoinObserver);

        Observer<Boolean> backPressObserver = backPressed -> {
            if (backPressed == null
                    || mainViewModel.getCurrentNavItem().getValue() == null) {
                return;
            }
            if (mainViewModel.getCurrentNavItem().getValue() != MainViewModel.NAV_ITEM_LISTING) {
                return;
            }
            if (listingViewModel.getSelectedCoin().getValue() != null) {
                listingViewModel.setSelectedCoin(null);
            } else {
                getActivity().finish();
            }
        };
        mainViewModel.getBackPressed().observe(getActivity(), backPressObserver);
    }

    private void showGeneralListing() {
        FragmentManager     cfm = getChildFragmentManager();
        FragmentTransaction cft = cfm.beginTransaction();
        for (Fragment fragment : cfm.getFragments()) {
            cft.remove(fragment);
        }
        cft.replace(R.id.container_listing, new ListingFragmentGeneral(), "listing_general");
        cft.commitNow();
    }

    private void showSpecificListing() {
        FragmentManager     cfm = getChildFragmentManager();
        FragmentTransaction cft = cfm.beginTransaction();
        for (Fragment fragment : cfm.getFragments()) {
            cft.remove(fragment);
        }
        cft.replace(R.id.container_listing, new ListingFragmentDetail(), "listing_specific");
        cft.commitNow();
    }
}
