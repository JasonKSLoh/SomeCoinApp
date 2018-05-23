package com.jason.experiment.somecoinapp.model.global;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * GlobalResponse
 * Created by jason.
 */
public class GlobalResponse {
    @SerializedName("globalStatus")
    @Expose
    private GlobalStatus   globalStatus;
    @SerializedName("globalDatum")
    @Expose
    private GlobalDatum    globalDatum;
    @SerializedName("globalMetadata")
    @Expose
    private GlobalMetadata globalMetadata;

    public GlobalStatus getGlobalStatus() {
        return globalStatus;
    }

    public void setGlobalStatus(GlobalStatus globalStatus) {
        this.globalStatus = globalStatus;
    }

    public GlobalDatum getGlobalDatum() {
        return globalDatum;
    }

    public void setGlobalDatum(GlobalDatum globalDatum) {
        this.globalDatum = globalDatum;
    }

    public GlobalMetadata getGlobalMetadata() {
        return globalMetadata;
    }

    public void setGlobalMetadata(GlobalMetadata globalMetadata) {
        this.globalMetadata = globalMetadata;
    }

}

