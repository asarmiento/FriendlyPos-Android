package com.friendlypos.application.interfaces;


import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.MetodoPagoResponse;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.PivotResponse;
import com.friendlypos.distribucion.modelo.ProductoFacturaResponse;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.distribucion.modelo.VentaResponse;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.login.modelo.UsuariosResponse;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.SysconfResponse;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
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

    @POST("/api/donwload-inventory-products")
    @FormUrlEncoded
    Call<Facturas> savePost(@Field("id") String id,
                            @Field("branch_office_id") String branch_office_id,
                            @Field("numeration") String numeration,
                            @Field("date") String date,
                            @Field("times") String times,
                            @Field("date_presale") String date_presale,
                            @Field("time_presale") String time_presale,
                            @Field("due_date") String due_date,
                            @Field("subtotal") String subtotal,
                            @Field("subtotal_taxed") String subtotal_taxed,
                            @Field("subtotal_exempt") String subtotal_exempt,
                            @Field("discount") String discount,
                            @Field("percent_discount") String percent_discount,
                            @Field("tax") String tax,
                            @Field("total") String total,
                            @Field("changing") String changing,
                            @Field("note") String note,
                            @Field("canceled") String canceled,
                            @Field("paid_up") String paid_up,
                            @Field("paid") String paid,
                            @Field("created_at") String created_at,
                            @Field("user_id") String user_id,
                            @Field("user_id_applied") String user_id_applied,
                            @Field("invoice_type_id") String invoice_type_id,
                            @Field("payment_method_id") String payment_method_id,
                            @Field("venta") String venta,
                            @Field("productofacturas") String productofacturas);

}
