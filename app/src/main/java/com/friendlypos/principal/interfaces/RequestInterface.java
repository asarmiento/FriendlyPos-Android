package com.friendlypos.principal.interfaces;


import com.friendlypos.principal.modelo.Productos;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {

   /* @GET("api/donwload-info-business")
    Call<AndroidVersion> getJSON();
*/
    @GET("api/donwload-info-business")
    Call<Productos> getJSON1();


}
