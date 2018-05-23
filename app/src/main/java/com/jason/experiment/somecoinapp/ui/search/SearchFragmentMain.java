package com.jason.experiment.somecoinapp.ui.search;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.logging.Logg;
import com.jason.experiment.somecoinapp.model.listing.ListingDatum;
import com.jason.experiment.somecoinapp.ui.MainViewModel;
import com.jason.experiment.somecoinapp.util.NetworkUtil;
import com.jason.experiment.somecoinapp.util.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchFragmentMain
 * Created by jason.
 */
public class SearchFragmentMain extends Fragment {

    MainViewModel   mainViewModel;
    SearchViewModel searchViewModel;

    RecyclerView rvSearch;
    EditText     etSearch;
    ImageView    ivSearch;

    SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_main, container, false);
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

        bindObservables();
    }

    private void bindViews(View view) {
        etSearch = view.findViewById(R.id.et_search_input);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                try{
                    InputMethodManager imm = (InputMethodManager)(getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (NullPointerException npe){
                    Logg.d(npe.getMessage());
                }
                searchViewModel.onSearchButtonPressed();
            }
            return true;
        });
        rvSearch = view.findViewById(R.id.rv_search_main);
        ivSearch = view.findViewById(R.id.iv_search_input);
        ivSearch.setOnClickListener(v -> {
            try{
                InputMethodManager imm = (InputMethodManager)(getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (NullPointerException npe){
                Logg.d( npe.getMessage());
            }
            searchViewModel.onSearchButtonPressed();
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchAdapter(new ArrayList<>());
        rvSearch.setAdapter(searchAdapter);
        rvSearch.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSearch.setLayoutManager(linearLayoutManager);
        rvSearch.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));
    }

    private void bindObservables() {
//        Observer<List<ListingDatum>> listingDataObserver = listingData -> {
//            if (listingData == null) {
//                return;
//            }
//            //TODO do something?
//        };
//        searchViewModel.getListingData().observe(this, listingDataObserver);

        Observer<List<ListingDatum>> searchedDataObserver = searchData -> {
            if (searchData== null) {
                return;
            } else if(searchViewModel.getListingData().getValue() == null){
                tryFetchListing();
            } else {
                searchAdapter.setEntries(searchData);
            }
        };
        searchViewModel.getSearchedData().observe(this, searchedDataObserver);


        Observer<Integer> focusedFragmentObserver = integer -> {
            if (integer == null || integer != MainViewModel.NAV_ITEM_SEARCH) {
                return;
            }
            tryFetchListing();
        };
        mainViewModel.getCurrentNavItem().observe(this, focusedFragmentObserver);

        Observer<Boolean> searchButtonObserver = bool -> {
            searchViewModel.findMatchingListings(etSearch.getText().toString());
        };
        searchViewModel.getSearchButtonPressed().observe(this, searchButtonObserver);
    }

    private void tryFetchListing() {
        Context context = getParentFragment().getContext();
        if (!NetworkUtil.isNetworkAvailable(context)) {
            searchViewModel.fetchLocalListing();
            Toast.makeText(getActivity(), "No Network Available. Fetching cached data", Toast.LENGTH_SHORT).show();
        } else if (SharedPrefsUtils.getListingLastUpdated(context) > System.currentTimeMillis() - SearchViewModel.RECENCY_THRESHOLD) {
            searchViewModel.fetchLocalListing();
        } else {
            searchViewModel.fetchRemoteListing();
            SharedPrefsUtils.setListingLastUpdated(context, System.currentTimeMillis());
        }
    }





    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<ListingDatum> entryList;

        SearchAdapter(List<ListingDatum> entries) {
            entryList = entries;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, final int position) {
            final ListingDatum currentEntry = entryList.get(position);
            holder.view.setOnClickListener(v -> {
                if(NetworkUtil.isNetworkAvailable(getContext())){
                    searchViewModel.fetchSelectedCoinRemote(currentEntry.getId());
                } else {
                    searchViewModel.fetchSelectedCoinLocal(currentEntry.getId());
                }
            });
            holder.displayData(currentEntry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

        public void setEntries(List<ListingDatum> entries) {
            this.entryList = entries;
            Logg.d( "Entries updated with num entries: " + entries.size());
            notifyDataSetChanged();
        }
    }

    private static class SearchViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        TextView tvId;
        TextView tvSymbol;
        TextView tvName;

        public SearchViewHolder(View view) {
            super(view);
            this.view = view;
            tvId = view.findViewById(R.id.tv_item_search_id);
            tvSymbol = view.findViewById(R.id.tv_item_search_symbol);
            tvName = view.findViewById(R.id.tv_item_search_name);
        }

        public void displayData(ListingDatum datum) {
            Logg.d( "Displaying data for datum: " + datum.getName());
            try {
                String id = datum.getId().toString();
                tvId.setText(id);
                tvSymbol.setText(datum.getSymbol());
                tvName.setText(datum.getName());

            } catch (NullPointerException npe) {
                tvId.setText("0");
                tvSymbol.setText("ERR");
                tvName.setText("An Error Occured");
            }
        }
    }
}
