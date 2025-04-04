package com.friendlysystemgroup.friendlypos.application.interfaces

import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.principal.modelo.Inventario
import com.friendlysystemgroup.friendlypos.principal.response.BonusesResponse
import com.friendlysystemgroup.friendlypos.principal.response.ClientesResponse
import com.friendlysystemgroup.friendlypos.principal.response.ConsecutivosNumberFeResponse
import com.friendlysystemgroup.friendlypos.principal.response.EmailResponse
import com.friendlysystemgroup.friendlypos.principal.response.FacturasResponse
import com.friendlysystemgroup.friendlypos.principal.response.InventarioResponse
import com.friendlysystemgroup.friendlypos.principal.response.MarcasResponse
import com.friendlysystemgroup.friendlypos.principal.response.MetodoPagoResponse
import com.friendlysystemgroup.friendlypos.principal.response.NumeracionResponse
import com.friendlysystemgroup.friendlypos.principal.response.ProductosResponse
import com.friendlysystemgroup.friendlypos.principal.response.RecibosResponse
import com.friendlysystemgroup.friendlypos.principal.response.SendEmailResponse
import com.friendlysystemgroup.friendlypos.principal.response.SysconfResponse
import com.friendlysystemgroup.friendlypos.principal.response.TipoProductoResponse
import com.friendlysystemgroup.friendlypos.principal.response.UserResponse
import com.friendlysystemgroup.friendlypos.principal.response.UsuariosResponse
import com.friendlysystemgroup.friendlypos.principal.modelo.customer_location
import com.friendlysystemgroup.friendlypos.principal.modelo.customer_new
import com.friendlysystemgroup.friendlypos.principal.modelo.email_Id
import com.friendlysystemgroup.friendlypos.principal.modelo.receipts
import com.friendlysystemgroup.friendlypos.principal.modelo.send_email_id
import com.friendlysystemgroup.friendlypos.principal.modelo.visit
import com.friendlysystemgroup.friendlypos.principal.helpers.EnviarClienteGPS
import com.friendlysystemgroup.friendlypos.principal.helpers.EnviarClienteNuevo
import com.friendlysystemgroup.friendlypos.principal.helpers.EnviarClienteVisitado
import com.friendlysystemgroup.friendlypos.principal.helpers.EnviarFactura
import com.friendlysystemgroup.friendlypos.principal.helpers.EnviarProductoDevuelto
import com.friendlysystemgroup.friendlypos.principal.helpers.EnviarRecibos
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RequestInterface {
    @POST("api/login")
    fun loginUser(@Body user: com.friendlypos.login.modelo.User?): Call<UserResponse?>?

    @POST("api/lista-facturas-lectronicas")
    fun savePostEmail(
        @Body customer: email_Id?,
        @Header("Authorization") token: String?
    ): Call<EmailResponse?>?

    @POST("api/invoice-reenvio-email-fe")
    fun savePostSendEmail(
        @Body invoice: send_email_id?,
        @Header("Authorization") token: String?
    ): Call<SendEmailResponse?>?

    @GET("api/donwload-catalogo")
    fun getJSON(@Header("Authorization") token: String?): Call<ClientesResponse?>?

    @GET("api/donwload-info-business")
    fun getSysconf(@Header("Authorization") token: String?): Call<SysconfResponse?>?

    @GET("api/donwload-info-business")
    fun getConsecutivosNumber(@Header("Authorization") token: String?): Call<ConsecutivosNumberFeResponse?>?

    @GET("api/donwload-catalogo")
    fun getProducts(@Header("Authorization") token: String?): Call<ProductosResponse?>?

    @GET("api/donwload-catalogo")
    fun getMarcas(@Header("Authorization") token: String?): Call<MarcasResponse?>?

    @GET("api/donwload-catalogo")
    fun getNumeracionDesc(@Header("Authorization") token: String?): Call<NumeracionResponse?>?

    @GET("api/donwload-catalogo")
    fun getBonusesTable(@Header("Authorization") token: String?): Call<BonusesResponse?>?

    @GET("api/donwload-catalogo")
    fun getTipoProducto(@Header("Authorization") token: String?): Call<TipoProductoResponse?>?

    @GET("api/donwload-catalogo")
    fun getMetodoPago(@Header("Authorization") token: String?): Call<MetodoPagoResponse?>?

    @GET("api/donwload-info-business")
    fun getUsuariosRetrofit(@Header("Authorization") token: String?): Call<UsuariosResponse?>?

    @GET("api/donwload-inventory-products")
    fun getInventory(@Header("Authorization") token: String?): Call<InventarioResponse?>?

    @GET("api/donwload-inventory-products")
    fun getFacturas(@Header("Authorization") token: String?): Call<FacturasResponse?>?

    @GET("api/download-customers-debts")
    fun getRecibos(@Header("Authorization") token: String?): Call<RecibosResponse?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-invoices")
    fun savePost(
        @Body invoice: EnviarFactura?,
        @Header("Authorization") token: String?
    ): Call<invoice?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-orders-presale")
    fun savePostPreventa(
        @Body invoice: EnviarFactura?,
        @Header("Authorization") token: String?
    ): Call<invoice?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-direct-sale")
    fun savePostVentaDirecta(
        @Body invoice: EnviarFactura?,
        @Header("Authorization") token: String?
    ): Call<invoice?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-proforma")
    fun savePostProforma(
        @Body invoice: EnviarFactura?,
        @Header("Authorization") token: String?
    ): Call<invoice?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-receipt")
    fun savePostRecibos(
        @Body receipts: EnviarRecibos?,
        @Header("Authorization") token: String?
    ): Call<receipts?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-visit")
    fun savePostClienteVisitado(
        @Body clienteVisitado: EnviarClienteVisitado?,
        @Header("Authorization") token: String?
    ): Call<visit?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("/api/upload-location-customer")
    fun savePostClienteGPS(
        @Body clienteVisitado: EnviarClienteGPS?,
        @Header("Authorization") token: String?
    ): Call<customer_location?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("api/upload-customer")
    fun savePostClienteNuevo(
        @Body clienteNuevo: EnviarClienteNuevo?,
        @Header("Authorization") token: String?
    ): Call<customer_new?>?

    // TODO DEFINIR CUAL ES EL KEY PARA ENVIAR (KEY, VALUE)
    @POST("api/upload-inventory-products")
    fun savePostProductoDevuelto(
        @Body clienteNuevo: EnviarProductoDevuelto?,
        @Header("Authorization") token: String?
    ): Call<Inventario?>?
}
