package com.friendlypos.application.interfaces;


import com.friendlypos.Recibos.modelo.EnviarRecibos;
import com.friendlypos.Recibos.modelo.RecibosResponse;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.MetodoPagoResponse;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.login.modelo.UsuariosResponse;
import com.friendlypos.preventas.modelo.BonusesResponse;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;
import com.friendlypos.preventas.modelo.NumeracionResponse;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.SysconfResponse;

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
    Call<SysconfResponse> getSysconf(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<ProductosResponse> getProducts(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<MarcasResponse> getMarcas(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<NumeracionResponse> getNumeracionDesc(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<BonusesResponse> getBonusesTable(@Header("Authorization") String token);

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

    @GET("api/download-customers-debts")
    Call<RecibosResponse> getRecibos(@Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-invoices")
    Call<invoice> savePost(@Body EnviarFactura invoice, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-orders-presale")
    Call<invoice> savePostPreventa(@Body EnviarFactura invoice, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-direct-sale")
    Call<invoice> savePostVentaDirecta(@Body EnviarFactura invoice, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-proforma")
    Call<invoice> savePostProforma(@Body EnviarFactura invoice, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-receipt")
    Call<receipts> savePostRecibos(@Body EnviarRecibos receipts, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-visit")
    Call<visit> savePostClienteVisitado(@Body EnviarClienteVisitado clienteVisitado, @Header("Authorization") String token);

}
