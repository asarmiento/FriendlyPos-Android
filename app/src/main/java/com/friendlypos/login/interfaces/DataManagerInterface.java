package com.friendlypos.login.interfaces;

import com.friendlypos.login.modelo.AppResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DataManagerInterface {
    @FormUrlEncoded
    @POST("api/login")
    Call<AppResponse> login(@Field("username") String user, @Field("password") String password);

  /*  @FormUrlEncoded
    @POST("api/login")
    Call<User> savePost(@Field("username") String user, @Field("password") String password);
*/

}