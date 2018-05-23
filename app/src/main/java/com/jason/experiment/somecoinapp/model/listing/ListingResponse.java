package com.jason.experiment.somecoinapp.model.listing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * ListingResponse
 * Created by jason.
 */
public class ListingResponse {
    @SerializedName("data")
    @Expose
    private List<ListingDatum> listingData = null;
    @SerializedName("listingMetadata")
    @Expose
    private ListingMetadata listingMetadata;

    public List<ListingDatum> getData() {
        return listingData;
    }

    public void setData(List<ListingDatum> data) {
        this.listingData = data;
    }

    public ListingMetadata getListingMetadata() {
        return listingMetadata;
    }

    public void setListingMetadata(ListingMetadata listingMetadata) {
        this.listingMetadata = listingMetadata;
    }

}
