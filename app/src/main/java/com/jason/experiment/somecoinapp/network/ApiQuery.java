package com.jason.experiment.somecoinapp.network;

import android.support.v4.util.Pair;

import java.util.ArrayList;

/**
 * ApiQuery
 * Created by jason.
 */
public class ApiQuery {

    private Integer limit    = null;
    private Integer start    = null;
    private Integer id       = null;
    private int     function = 0;
    private String  currency = null;

    private static final String LIMIT = "limit";
    private static final String CURRENCY = "convert";
    private static final String START = "start";
    private static final int LIMIT_MAX = 100;

    public Integer getLimit() {
        return limit;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getId() {
        return id;
    }

    public int getFunction() {
        return function;
    }

    public String getCurrency() {
        return currency;
    }

    public ApiQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ApiQuery setStart(Integer start) {
        this.start = start;
        return this;
    }

    public ApiQuery setId(Integer id) {
        this.id = id;
        return this;
    }

    public ApiQuery setFunction(int function) {
        this.function = function;
        return this;
    }

    public ApiQuery setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String generateUrl() {
        StringBuilder endpoint;
        switch (function) {
            case FUNCS.LISTINGS:
                endpoint = new StringBuilder(NetworkConsts.LISTING_ENDPOINT);
                return endpoint.toString();
            case FUNCS.TICKER:
                endpoint = new StringBuilder(NetworkConsts.TICKER_ENDPOINT);
                if (id != null) {
                    endpoint.append("/")
                            .append(id.toString());
                }
                break;
            case FUNCS.GLOBAL_DATA:
                endpoint = new StringBuilder(NetworkConsts.GLOBAL_DATA_ENDPOINT);
                if (currency != null) {
                    endpoint.append("?")
                            .append(CURRENCY)
                            .append("=")
                            .append(currency);
                }
                return endpoint.toString();
            default:
                return null;
        }

        ArrayList<Pair<String, String>> urlParams = new ArrayList<>();


        endpoint.append("?");
        if (start != null
                && start >= 0
                && start <= LIMIT_MAX) {
            urlParams.add(new Pair<>(START, start.toString()));
        }
        if(limit != null
                && limit > 0
                && limit <= LIMIT_MAX ){
            urlParams.add(new Pair<>(LIMIT, limit.toString()));
        }
        if(currency != null){
            urlParams.add(new Pair<>(CURRENCY, currency));
        }

        for(int i = 0; i < urlParams.size(); i++){
            if(i == 0){
                endpoint.append(urlParams.get(i).first)
                        .append("=")
                        .append(urlParams.get(i).second);
            } else {
                endpoint.append("&")
                        .append(urlParams.get(i).first)
                        .append("=")
                        .append(urlParams.get(i).second);
            }
        }

        return endpoint.toString();
    }

    public static class FUNCS {
        public static final int LISTINGS    = 0;
        public static final int TICKER      = 1;
        public static final int GLOBAL_DATA = 2;
    }

}
