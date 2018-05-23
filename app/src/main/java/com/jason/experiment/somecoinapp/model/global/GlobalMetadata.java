package com.jason.experiment.somecoinapp.model.global;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * GlobalMetadata
 * Created by jason.
 */
public class GlobalMetadata {
    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;
    @SerializedName("error")
    @Expose
    private Object error;

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
