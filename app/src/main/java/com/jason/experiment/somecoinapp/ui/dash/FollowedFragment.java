package com.jason.experiment.somecoinapp.ui.dash;

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
 * FollowedFragment
 * Created by jason.
 */
public class FollowedFragment extends Fragment {

    MainViewModel     mainViewModel;
    FollowedViewModel followedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followed, container, false);
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
        followedViewModel = ViewModelProviders.of(this, new FollowedViewModel.Factory(remoteSource, localRepo)).get(FollowedViewModel.class);

        bindObservers();
    }

    public void bindObservers() {
        Observer<TickerDatum> selectedCoinObserver = tickerDatum -> {
            if (tickerDatum == null) {
                showGeneralFollowed();
            } else {
                showDetailFollowed();
            }
        };
        followedViewModel.getSelectedCoin().observe(this, selectedCoinObserver);

        Observer<Boolean> backPressObserver = backPressed -> {
            if (backPressed == null
                    || mainViewModel.getCurrentNavItem().getValue() == null) {
                return;
            }
            if (mainViewModel.getCurrentNavItem().getValue() != MainViewModel.NAV_ITEM_FOLLOWED) {
                return;
            }
            if (followedViewModel.getSelectedCoin().getValue() != null) {
                followedViewModel.setSelectedCoin(null);
            } else {
                getActivity().finish();
            }
        };
        mainViewModel.getBackPressed().observe(getActivity(), backPressObserver);
    }


    private void showGeneralFollowed() {
        FragmentManager     cfm = getChildFragmentManager();
        FragmentTransaction cft = cfm.beginTransaction();
        for (Fragment fragment : cfm.getFragments()) {
            cft.remove(fragment);
        }
        cft.replace(R.id.container_followed, new FollowedFragmentGeneral(), "followed_general");
        cft.commitNow();
    }

    private void showDetailFollowed() {
        FragmentManager     cfm = getChildFragmentManager();
        FragmentTransaction cft = cfm.beginTransaction();
        for (Fragment fragment : cfm.getFragments()) {
            cft.remove(fragment);
        }
        cft.replace(R.id.container_followed, new FollowedFragmentDetail(), "followed_detail");
        cft.commitNow();
    }
}
