package com.friendlypos.login.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jd on 7/27/17.
 */

public class GetStypeResponse {

    @SerializedName("Result")
    List<GetStypeItemResponse> mResults;

    public GetStypeResponse(List<GetStypeItemResponse> responseList) {
        this.mResults = responseList;
    }

    public List<GetStypeItemResponse> getResults() {
        return mResults;
    }

    public void setResults(List<GetStypeItemResponse> mResults) {
        this.mResults = mResults;
    }
}
