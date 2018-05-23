package com.jason.experiment.somecoinapp.ui.search;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerQuote;
import com.jason.experiment.somecoinapp.ui.MainViewModel;
import com.jason.experiment.somecoinapp.util.SharedPrefsUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * SearchFragmentDetail
 * Created by jason.
 */
public class SearchFragmentDetail extends Fragment {

    TextView  tvRank;
    TextView  tvSymbol;
    TextView  tvName;
    TextView  tvPrice;
    TextView  tvMarketCap;
    TextView  tv1hrChange;
    TextView  tv24hrChange;
    TextView  tv7dChange;
    TextView  tvCurrency;
    TextView  tvVolume;
    TextView  tvTotalSupply;
    TextView  tvCirculatingSupply;
    TextView  tvMaxSupply;
    ImageView iv1hrChange;
    ImageView iv24hrChange;
    ImageView iv7dChange;
    Button    btnFollow;

    MainViewModel     mainViewModel;
    SearchViewModel searchViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listing_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        searchViewModel = ViewModelProviders.of(getParentFragment()).get(SearchViewModel.class);
        bindObservers();
    }

    private void bindObservers() {
        Observer<TickerDatum> selectedCoinObserver = selectedCoin -> {
            populateView(selectedCoin);
        };
        searchViewModel.getSelectedCoin().observe(getParentFragment(), selectedCoinObserver);

        Observer<Boolean> followButtonObserver = b -> {
            if (b == null) {
                return;
            }
            int coinId = searchViewModel.getSelectedCoin().getValue().getId();
            if(isCoinFollowed()){
                SharedPrefsUtils.unsaveCoin(getParentFragment().getContext(), coinId);
                setBtnFollowToValue(false);
            } else {
                SharedPrefsUtils.saveCoin(getParentFragment().getContext(), coinId);
                setBtnFollowToValue(true);
            }
        };
        searchViewModel.getFollowButtonPressed().observe(getParentFragment(), followButtonObserver);
    }

    private void bindViews(View view) {
        tvRank = view.findViewById(R.id.tv_detail_rank);
        tvSymbol = view.findViewById(R.id.tv_detail_symbol);
        tvName = view.findViewById(R.id.tv_detail_name);
        tvPrice = view.findViewById(R.id.tv_detail_price);
        tvMarketCap = view.findViewById(R.id.tv_detail_marketcap);
        tv1hrChange = view.findViewById(R.id.tv_detail_1hr_change);
        tv24hrChange = view.findViewById(R.id.tv_detail_24hr_change);
        tv7dChange = view.findViewById(R.id.tv_detail_7d_change);
        tvCurrency = view.findViewById(R.id.tv_detail_price_currency);
        tvVolume = view.findViewById(R.id.tv_detail_volume);
        tvTotalSupply = view.findViewById(R.id.tv_detail_total_supply);
        tvMaxSupply = view.findViewById(R.id.tv_detail_max_supply);
        tvCirculatingSupply = view.findViewById(R.id.tv_detail_circulating_supply);
        iv1hrChange = view.findViewById(R.id.iv_detail_1hr_change);
        iv24hrChange = view.findViewById(R.id.iv_detail_24hr_change);
        iv7dChange = view.findViewById(R.id.iv_detail_7d_change);
        btnFollow = view.findViewById(R.id.btn_detail_follow);

        btnFollow.setOnClickListener(v -> {
            searchViewModel.onFollowButtonPressed();
        });


    }

    private void setBtnFollowToValue(boolean isFollow){
        if(!isFollow){
            btnFollow.setBackgroundColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_green_400));
            btnFollow.setText(R.string.follow);
        } else {
            btnFollow.setBackgroundColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_red_400));
            btnFollow.setText(R.string.unfollow);
        }
    }

    private boolean isCoinFollowed(){
        Fragment parent = getParentFragment();
        if(parent == null){
            return false;
        }
        Context            context       = getParentFragment().getContext();
        ArrayList<Integer> savedCoins    = SharedPrefsUtils.getSavedCoins(context);
        TickerDatum        selectedDatum = searchViewModel.getSelectedCoin().getValue();
        if(selectedDatum == null){
            return false;
        }
        int coinId = searchViewModel.getSelectedCoin().getValue().getId();
        return savedCoins != null && savedCoins.size() >= 1 && savedCoins.contains(coinId);

    }

    private void populateView(TickerDatum datum) {
        if (datum == null) {
            return;
        }
        tvRank.setText(datum.getRank().toString());
        tvSymbol.setText(datum.getSymbol());
        tvName.setText(datum.getName());

        String      currency       = datum.getQuotes().keySet().toArray()[0].toString();
        TickerQuote quote          = datum.getQuotes().get(currency);
        Double      rawPrice       = quote.getPrice();
        String      formattedPrice = String.format(Locale.getDefault(), "%.2f", rawPrice);
        tvPrice.setText(formattedPrice);
        tvCurrency.setText(currency);

        Double rawMarketCap       = quote.getMarketCap();
        String formattedMarketCap = new DecimalFormat("#,###").format(rawMarketCap);
        String marketCapText      = formattedMarketCap + " " + currency;
        tvMarketCap.setText(marketCapText);

        Double rawVolume       = quote.getVolume24h();
        String formattedVolume = new DecimalFormat("#,###").format(rawVolume);
        String volumeText      = formattedVolume + " " + currency;
        tvVolume.setText(volumeText);

        Double change1hrDouble = quote.getPercentChange1h();
        String change1hr       = change1hrDouble.toString();
        tv1hrChange.setText(change1hr);
        if (change1hrDouble > 0) {
            tv1hrChange.setTextColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_green_400));
            iv1hrChange.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
        } else if (change1hrDouble < 0) {
            tv1hrChange.setTextColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_red_400));
            iv1hrChange.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
        }

        Double change24hrDouble = quote.getPercentChange24h();
        String change24hr       = change24hrDouble.toString();
        tv24hrChange.setText(change24hr);
        if (change24hrDouble > 0) {
            tv24hrChange.setTextColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_green_400));
            iv24hrChange.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
        } else if (change24hrDouble < 0) {
            tv24hrChange.setTextColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_red_400));
            iv24hrChange.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
        }

        Double change7dDouble = quote.getPercentChange7d();
        String change7d       = change7dDouble.toString();
        tv7dChange.setText(change7d);
        if (change7dDouble > 0) {
            tv7dChange.setTextColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_green_400));
            iv7dChange.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
        } else if (change7dDouble < 0) {
            tv7dChange.setTextColor(ContextCompat.getColor(getParentFragment().getContext(), R.color.material_red_400));
            iv7dChange.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
        }

        Double rawCirculatingSupply = datum.getCirculatingSupply();
        setDecimalFormat(tvCirculatingSupply, rawCirculatingSupply, "#,###.#");

        Double rawTotalSupply = datum.getTotalSupply();
        setDecimalFormat(tvTotalSupply, rawTotalSupply, "#,###.#");

        Double rawMaxSupply = datum.getMaxSupply();
        setDecimalFormat(tvMaxSupply, rawMaxSupply, "#,###.#");
    }

    private void setDecimalFormat(TextView tv, Double input, String pattern){
        try{
            String formattedInput = new DecimalFormat(pattern).format(input);
            tv.setText(formattedInput);
        }catch (Exception e){
            tv.setText(String.format(Locale.getDefault(), "%.1f", input));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isCoinFollowed()){
            setBtnFollowToValue(true);
        } else {
            setBtnFollowToValue(false);
        }
    }

}
