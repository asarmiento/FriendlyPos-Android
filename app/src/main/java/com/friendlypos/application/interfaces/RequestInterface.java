package com.friendlypos.application.interfaces;


import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.PivotResponse;
import com.friendlypos.distribucion.modelo.ProductoFacturaResponse;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.distribucion.modelo.VentaResponse;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.SysconfResponse;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @GET("api/donwload-inventory-products")
    Call<InventarioResponse> getInventory(@Header("Authorization") String token);

    @GET("api/donwload-inventory-products")
    Call<FacturasResponse> getFacturas(@Header("Authorization") String token);

    @GET("api/donwload-inventory-products")
    Call<VentaResponse> getVentas(@Header("Authorization") String token);

    @GET("api/donwload-inventory-products")
    Call<PivotResponse> getPivot(@Header("Authorization") String token, @Query("invoice_id") String invoice_id );

    @GET("api/donwload-inventory-products")
    Call<ProductoFacturaResponse> getProductofactura(@Header("Authorization") String token, @Query("invoice_id") String invoice_id );


}
