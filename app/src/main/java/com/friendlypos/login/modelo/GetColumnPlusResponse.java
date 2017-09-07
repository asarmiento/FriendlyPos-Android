package com.friendlypos.login.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by juandiegoGL on 7/3/17.
 */

/**
 * Created by juandiegoGL on 6/18/17.
 */

public class GetColumnPlusResponse extends SaviorDataResponse {

    @SerializedName("Result")
    List<GetColumnPlusItemResponse> mResults;

    @SerializedName("TotalCount")
    @Expose
    private Map<String, String> total_count;

    public GetColumnPlusResponse(List<GetColumnPlusItemResponse> results, String latestUTCT, String error, String mac, Map<String, String> total_count) {
        super(error, latestUTCT, mac);
        mResults = results;
        this.total_count = total_count;
    }

    public void setResults(List<GetColumnPlusItemResponse> mResults) {
        this.mResults = mResults;
    }

    public Map<String, String> getTotalCount() {
        return total_count;
    }

    public List<GetColumnPlusItemResponse> getResults() {
        return mResults;
    }

    @Override
    public String toString() {
        return "GetColumnPlusResponse{" +
            "mResults=" + mResults +
            ", total_count=" + total_count +
            '}';
    }
}