package com.jason.experiment.somecoinapp.ui.dash;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerQuote;
import com.jason.experiment.somecoinapp.ui.MainViewModel;
import com.jason.experiment.somecoinapp.util.NetworkUtil;
import com.jason.experiment.somecoinapp.util.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * FollowedFragmentGeneral
 * Created by jason.
 */
public class FollowedFragmentGeneral extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView       rvFollowed;
    FollowedAdapter    followedAdapter;
    Button             btnFollowedClear;

    MainViewModel     mainViewModel;
    FollowedViewModel followedViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followed_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFollowed = view.findViewById(R.id.rv_followed);
        bindViews(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        followedViewModel = ViewModelProviders.of(getParentFragment()).get(FollowedViewModel.class);

        bindObservables();
    }

    private void bindViews(View view) {
        btnFollowedClear = view.findViewById(R.id.btn_followed_clear);
        btnFollowedClear.setOnClickListener(v -> {
            showUnfollowConfirmationDialog();
        });
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_listing_general);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            tryFetchTickerData();
        });
        setupRecyclerView();
    }

    private void showUnfollowConfirmationDialog(){
        Context context = getParentFragment().getContext();
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Unfollow all Coins")
                .setMessage("Are you sure you want to unfollow all coins?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPrefsUtils.unsaveAllCoins(context);
                    followedViewModel.setFollowedCoinData(new ArrayList<>());
                    Toast.makeText(context, "All coins unfollowed.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }).setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                }).setCancelable(true)
                .show();
    }

    private void tryFetchTickerData() {
        ArrayList<Integer> followedCoins = SharedPrefsUtils.getSavedCoins(getActivity());
        if (followedCoins == null || followedCoins.isEmpty()) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            followedViewModel.fetchFollowedCoinsLocal(followedCoins);
            Toast.makeText(getActivity(), "No Network Available. Fetching cached data", Toast.LENGTH_SHORT).show();
        }
        followedViewModel.fetchFollowedCoinsRemote(followedCoins);
    }

    private void setupRecyclerView() {
        followedAdapter = new FollowedAdapter(new ArrayList<>());
        rvFollowed.setAdapter(followedAdapter);
        rvFollowed.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFollowed.setLayoutManager(linearLayoutManager);
        rvFollowed.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));
    }

    private void bindObservables() {
        Observer<List<TickerDatum>> followedDataObserver = tickerData -> {
            if (tickerData == null) {
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
            followedAdapter.setEntries(tickerData);
        };
        followedViewModel.getFollowedCoinData().observe(this, followedDataObserver);

        Observer<Integer> focusedFragmentObserver = integer -> {
            if (integer == null || integer != MainViewModel.NAV_ITEM_FOLLOWED) {
                return;
            }
            tryFetchTickerData();
        };

        mainViewModel.getCurrentNavItem().observe(this, focusedFragmentObserver);
    }

    private class FollowedAdapter extends RecyclerView.Adapter<FollowedViewHolder> {
        private List<TickerDatum> entryList;

        FollowedAdapter(List<TickerDatum> entries) {
            entryList = entries;
        }

        @NonNull
        @Override
        public FollowedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following, parent, false);
            return new FollowedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FollowedViewHolder holder, final int position) {
            final TickerDatum currentEntry = entryList.get(position);
            holder.view.setOnClickListener(v -> {
                followedViewModel.setSelectedCoin(currentEntry);
            });
            holder.ivFollowUnfollow.setOnClickListener(v -> {
                if(SharedPrefsUtils.isCoinSaved(getContext(), currentEntry.getId())){
                    SharedPrefsUtils.unsaveCoin(getContext(), currentEntry.getId());
                    holder.ivFollowUnfollow.setBackgroundResource(R.drawable.ic_follow_24dp);
                } else {
                    SharedPrefsUtils.saveCoin(getContext(), currentEntry.getId());
                    holder.ivFollowUnfollow.setBackgroundResource(R.drawable.ic_unfollow_24dp);
                }
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

    private static class FollowedViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        TextView  tvRank;
        TextView  tvSymbol;
        TextView  tvPrice;
        TextView tvPriceChange24;
        ImageView ivPriceChange24;
        TextView tvPriceChange7d;
        ImageView ivPriceChange7d;
        ImageView ivFollowUnfollow;

        public FollowedViewHolder(View view) {
            super(view);
            this.view = view;
            tvRank = view.findViewById(R.id.tv_item_following_rank);
            tvSymbol = view.findViewById(R.id.tv_item_following_symbol);
            tvPrice = view.findViewById(R.id.tv_item_following_price);
            tvPriceChange24 = view.findViewById(R.id.tv_item_following_change24);
            ivPriceChange24 = view.findViewById(R.id.iv_item_following_change24);
            tvPriceChange7d = view.findViewById(R.id.tv_item_following_change7d);
            ivPriceChange7d = view.findViewById(R.id.iv_item_following_change7d);
            ivFollowUnfollow = view.findViewById(R.id.iv_item_following_followunfollow);
        }

        public void displayData(TickerDatum datum) {
            try {
                String rank = datum.getRank().toString();
                tvRank.setText(rank);
                tvSymbol.setText(datum.getSymbol());

                String      currency       = datum.getQuotes().keySet().toArray()[0].toString();
                TickerQuote quote          = datum.getQuotes().get(currency);
                Double      rawPrice       = quote.getPrice();
                String      formattedPrice = String.format(Locale.getDefault(), "%.2f", rawPrice);
                tvPrice.setText(formattedPrice);

                Double priceChange = datum.getQuotes().get(currency).getPercentChange24h();

                Double change24hrDouble = quote.getPercentChange24h();
                String change24hr       = change24hrDouble.toString();
                tvPriceChange24.setText(change24hr);
                if (change24hrDouble > 0) {
                    tvPriceChange24.setTextColor(ContextCompat.getColor(view.getContext(), R.color.material_green_400));
                    ivPriceChange24.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else if (change24hrDouble < 0) {
                    tvPriceChange24.setTextColor(ContextCompat.getColor(view.getContext(), R.color.material_red_400));
                    ivPriceChange24.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
                }

                Double change7dDouble = quote.getPercentChange7d();
                String change7d       = change7dDouble.toString();
                tvPriceChange7d.setText(change7d);
                if (change7dDouble > 0) {
                    tvPriceChange7d.setTextColor(ContextCompat.getColor(view.getContext(), R.color.material_green_400));
                    ivPriceChange7d.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else if (change7dDouble < 0) {
                    tvPriceChange7d.setTextColor(ContextCompat.getColor(view.getContext(), R.color.material_red_400));
                    ivPriceChange7d.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
                }

                if(SharedPrefsUtils.isCoinSaved(view.getContext(), datum.getId())){
                    ivFollowUnfollow.setBackgroundResource(R.drawable.ic_unfollow_24dp);
                } else {
                    ivFollowUnfollow.setBackgroundResource(R.drawable.ic_follow_24dp);
                }
            } catch (NullPointerException npe) {
                tvRank.setText("0");
                tvSymbol.setText("ERR");
                tvPrice.setText("USD0.00");
            }
        }

    }
}
