package com.friendlypos.application.interfaces;


import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.MetodoPagoResponse;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.login.modelo.UsuariosResponse;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.SysconfResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestInterface {

    @POST("api/login")
    Call<UserResponse> loginUser(@Body User user);

    @GET("api/donwload-info-business")
    Call<ClientesResponse> getJSON(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<SysconfResponse> getSysconf(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<ProductosResponse> getProducts(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<MarcasResponse> getMarcas(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<TipoProductoResponse> getTipoProducto(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<MetodoPagoResponse> getMetodoPago(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<UsuariosResponse> getUsuariosRetrofit(@Header("Authorization") String token);

    @GET("api/donwload-inventory-products")
    Call<InventarioResponse> getInventory(@Header("Authorization") String token);

    @GET("api/donwload-inventory-products")
    Call<FacturasResponse> getFacturas(@Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-invoices")
    Call<Facturas> savePost(@Body Facturas facturas, @Header("Authorization") String token);

}
