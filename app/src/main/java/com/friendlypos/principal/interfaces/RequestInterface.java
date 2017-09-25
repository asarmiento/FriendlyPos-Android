package com.friendlypos.principal.interfaces;


import com.friendlypos.login.modelo.AppResponse;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RequestInterface {

    @FormUrlEncoded
    @POST("api/login")
    Call<AppResponse> login(@Field("username") String user, @Field("password") String password);
    @GET("api/donwload-info-business")
    Call<ClientesResponse> getJSON();

    @GET("api/donwload-info-business")
    Call<ProductosResponse> getProducts();


}
