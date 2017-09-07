package com.friendlypos.login.interfaces;

import com.example.app.savior.model.GetAlarmResponse;
import com.example.app.savior.model.GetColumnPlusResponse;
import com.example.app.savior.model.GetColumnResponse;
import com.example.app.savior.model.GetStypeResponse;
import com.example.app.savior.model.GetTemperatureResponse;
import com.example.app.savior.model.SaviorResponse;

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
    @POST("logU")
    Call<SaviorResponse> login(@Field("U") String user, @Field("P") String password);

    @FormUrlEncoded
    @POST("regU")
    Call<SaviorResponse> register(@Field("U") String user, @Field("P") String password);

    @FormUrlEncoded
    @POST("AsUMac")
    Call<SaviorResponse> assignMacAddress(@Field("U") String user, @Field("MAC") String mac_address);

    @GET("GetT")
    Call<GetTemperatureResponse> getTemperature(@Query("U") String user, @Query("MAC") String mac_address, @Query("UTCT") String date);

    @GET("GetA")
    Call<GetAlarmResponse> getAlarm(@Query("U") String user, @Query("MAC") String mac_address, @Query("UTCT") String date);

    @GET("GetColumns")
    Call<GetColumnResponse> getColumns(@Query("U") String user, @Query("MACList") String mac_address_list, @Query("UTCT") String date, @Query("Cols") String sort);

    @GET("GetColumnsPlus_F")
    Call<GetColumnPlusResponse> getColumnsPlus(@Query("U") String user, @Query("MACList") String mac_address_list, @Query("UTCT") String date);

    @GET("GetStype")
    Call<GetStypeResponse> getStype(@Query("U") String user, @Query("MACList") String mac_address_list);
}