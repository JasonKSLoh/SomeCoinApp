package com.jason.experiment.somecoinapp.ui.listing;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerQuote;
import com.jason.experiment.somecoinapp.ui.MainViewModel;
import com.jason.experiment.somecoinapp.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ListingFragmentGeneral
 * Created by jason.
 */
public class ListingFragmentGeneral extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView       rvListing;
    ListingAdapter     listingAdapter;

    MainViewModel    mainViewModel;
    ListingViewModel listingViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listing_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListing = view.findViewById(R.id.rv_listing);
        bindViews(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        listingViewModel = ViewModelProviders.of(getParentFragment()).get(ListingViewModel.class);

        bindObservables();
        tryFetchTickerData();
    }

    private void bindViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_listing_general);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tryFetchTickerData();
        });
        setupRecyclerView();
    }

    private void tryFetchTickerData() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            listingViewModel.fetchTickerDataRemote();
        } else {
            listingViewModel.fetchTickerDataLocal();
            Toast.makeText(getContext(), R.string.network_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        listingAdapter = new ListingAdapter(new ArrayList<>());
        rvListing.setAdapter(listingAdapter);
        rvListing.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvListing.setLayoutManager(linearLayoutManager);
        rvListing.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));
    }

    private void bindObservables() {
        Observer<List<TickerDatum>> tickerObserver = tickerData -> {
            swipeRefreshLayout.setRefreshing(false);
            listingAdapter.setEntries(tickerData);
        };
        listingViewModel.getTickerData().observe(this, tickerObserver);
    }

    private class ListingAdapter extends RecyclerView.Adapter<ListingViewHolder> {
        private List<TickerDatum> entryList;

        ListingAdapter(List<TickerDatum> entries) {
            entryList = entries;
        }

        @NonNull
        @Override
        public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
            return new ListingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListingViewHolder holder, final int position) {
            final TickerDatum currentEntry = entryList.get(position);
            holder.view.setOnClickListener(v -> {
                listingViewModel.setSelectedCoin(currentEntry);
            });

            holder.displayData(currentEntry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

        public void setEntries(List<TickerDatum> entries) {
            this.entryList = entries;
            notifyDataSetChanged();
        }
    }

    private static class ListingViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        TextView  tvRank;
        TextView  tvSymbol;
        TextView  tvName;
        TextView  tvPrice;
        ImageView ivPriceChange;

        public ListingViewHolder(View view) {
            super(view);
            this.view = view;
            tvRank = view.findViewById(R.id.tv_item_listing_rank);
            tvSymbol = view.findViewById(R.id.tv_item_listing_symbol);
            tvName = view.findViewById(R.id.tv_item_listing_name);
            tvPrice = view.findViewById(R.id.tv_item_listing_price);
            ivPriceChange = view.findViewById(R.id.iv_pricechange_arrow);
        }

        public void displayData(TickerDatum datum) {
            try {
                String rank = datum.getRank().toString();
                tvRank.setText(rank);
                tvSymbol.setText(datum.getSymbol());
                tvName.setText(datum.getName());

                String currency  = datum.getQuotes().keySet().toArray()[0].toString();
                String rawAmount = datum.getQuotes().get(currency).getPrice().toString();

                TickerQuote quote          = datum.getQuotes().get(currency);
                Double      rawPrice       = quote.getPrice();
                String      formattedPrice = String.format(Locale.getDefault(), "%.2f", rawPrice);
                tvPrice.setText(formattedPrice);


                Double priceChange = datum.getQuotes().get(currency).getPercentChange24h();

                if (priceChange > 0) {
                    ivPriceChange.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else if (priceChange < 0) {
                    ivPriceChange.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    ivPriceChange.setBackgroundResource(0);
                }
            } catch (NullPointerException npe) {
                tvRank.setText("0");
                tvSymbol.setText("ERR");
                tvName.setText("An Error Occured");
                tvPrice.setText("USD0.00");
            }
        }
    }


}
