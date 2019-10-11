package com.friendlypos.application.interfaces;


import com.friendlypos.Recibos.modelo.EnviarRecibos;
import com.friendlypos.Recibos.modelo.RecibosResponse;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.crearCliente.modelo.customer_new;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
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
import com.friendlypos.principal.modelo.ConsecutivosNumberFeResponse;
import com.friendlypos.principal.modelo.EnviarClienteGPS;
import com.friendlypos.principal.modelo.EnviarClienteNuevo;
import com.friendlypos.principal.modelo.EnviarProductoDevuelto;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.SysconfResponse;
import com.friendlypos.principal.modelo.customer_location;
import com.friendlypos.reenvio_email.modelo.EmailResponse;
import com.friendlypos.reenvio_email.modelo.SendEmailResponse;
import com.friendlypos.reenvio_email.modelo.email_Id;
import com.friendlypos.reenvio_email.modelo.send_email_id;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("api/login")
    Call<UserResponse> loginUser(@Body User user);

    @POST("api/lista-facturas-lectronicas")
    Call<EmailResponse> savePostEmail(@Body email_Id customer, @Header("Authorization") String token);

    @POST("api/invoice-reenvio-email-fe")
    Call<SendEmailResponse> savePostSendEmail(@Body send_email_id invoice, @Header("Authorization") String token);

    @GET("api/donwload-catalogo")
    Call<ClientesResponse> getJSON(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<SysconfResponse> getSysconf(@Header("Authorization") String token);

    @GET("api/donwload-info-business")
    Call<ConsecutivosNumberFeResponse> getConsecutivosNumber(@Header("Authorization") String token);

    @GET("api/donwload-catalogo")
    Call<ProductosResponse> getProducts(@Header("Authorization") String token);

    @GET("api/donwload-catalogo")
    Call<MarcasResponse> getMarcas(@Header("Authorization") String token);

    @GET("api/donwload-catalogo")
    Call<NumeracionResponse> getNumeracionDesc(@Header("Authorization") String token);

    @GET("api/donwload-catalogo")
    Call<BonusesResponse> getBonusesTable(@Header("Authorization") String token);

    @GET("api/donwload-catalogo")
    Call<TipoProductoResponse> getTipoProducto(@Header("Authorization") String token);

    @GET("api/donwload-catalogo")
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

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-location-customer")
    Call<customer_location> savePostClienteGPS(@Body EnviarClienteGPS clienteVisitado, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("api/upload-customer")
    Call<customer_new> savePostClienteNuevo(@Body EnviarClienteNuevo clienteNuevo, @Header("Authorization") String token);

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("api/upload-inventory-products")
    Call<Inventario> savePostProductoDevuelto(@Body EnviarProductoDevuelto clienteNuevo, @Header("Authorization") String token);
}
