package com.friendlypos.application.interfaces;


import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.ProductosResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("api/login")
    Call<UserResponse> loginUser(@Body User user);

    @GET("api/donwload-info-business")
    Call<ClientesResponse> getJSON(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<ProductosResponse> getProducts(@Header("Authorization") String token);

    @GET("api/donwload-inventory-products")
    Call<ProductosResponse> getInventory(@Header("Authorization") String token);




}
