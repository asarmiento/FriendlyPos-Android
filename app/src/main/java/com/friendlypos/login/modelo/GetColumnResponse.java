package com.friendlypos.login.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by juandiegoGL on 6/18/17.
 */

public class GetColumnResponse extends SaviorDataResponse {

    @SerializedName("Result")
    List<GetColumnItemResponse> mResults;

    public GetColumnResponse(List<GetColumnItemResponse> results, String latestUTCT, String error, String mac) {
        super(error, latestUTCT, mac);
        mResults = results;
    }

    public List<GetColumnItemResponse> getResults() {
        return mResults;
    }

    @Override
    public String toString() {
        return "GetColumnResponse{" +
            "mResults=" + mResults +
            '}';
    }
}