package com.friendlypos.principal.interfaces;


import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET("api/donwload-info-business")
    Call<ClientesResponse> getJSON();

    @GET("api/donwload-info-business")
    Call<ProductosResponse> getProducts();


}
