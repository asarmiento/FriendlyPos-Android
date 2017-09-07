package com.friendlypos.login.interfaces;

import com.friendlypos.login.modelo.AppResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public interface DataManagerInterface {
    @FormUrlEncoded
    @POST("api/login")
    Call<AppResponse> login(@Field("U") String user, @Field("P") String password);
}