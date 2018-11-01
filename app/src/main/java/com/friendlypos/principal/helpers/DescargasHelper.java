package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.Recibos.modelo.RecibosResponse;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.MetodoPago;
import com.friendlypos.distribucion.modelo.MetodoPagoResponse;
import com.friendlypos.distribucion.modelo.ProductoFactura;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.modelo.UsuariosResponse;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.preventas.modelo.BonusesResponse;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.preventas.modelo.NumeracionResponse;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.Sysconf;
import com.friendlypos.principal.modelo.SysconfResponse;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DelvoM on 03/11/2017.
 */

public class DescargasHelper {
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Activity activity;
    private Context mContext;
    private Realm realm, realm2, realmSysconfig, realmRecibos, realmMarcas, realmNumeracion, realmBonuses,  realmTipoProducto,realmUsuarios, realmMetodoPago;

    public DescargasHelper(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
    }

    public void descargarCatalogo(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<Clientes> mContentsArray = new ArrayList<>();
        final ArrayList<Productos> mContentsArray2 = new ArrayList<>();
        final ArrayList<Marcas> mContentsArrayMarcas = new ArrayList<>();
        final ArrayList<Numeracion> mContentsArrayNumeracion = new ArrayList<>();
        final ArrayList<Bonuses> mContentsArrayBonuses = new ArrayList<>();
        final ArrayList<TipoProducto> mContentsArrayTipoProducto = new ArrayList<>();
        final ArrayList<MetodoPago> mContentsArrayMetodoPago = new ArrayList<>();
        final ArrayList<Usuarios> mContentsArrayUsuarios = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando lista de catálogo");

        if (isOnline()) {
            dialog.show();

            // TODO descarga Clientes
            Call<ClientesResponse> call = api.getJSON(token);

            call.enqueue(new Callback<ClientesResponse>() {

                @Override
                public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {
                    mContentsArray.clear();
                    realm = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArray.addAll(response.body().getContents());

                        try {


                            // Work with Realm
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(mContentsArray);
                            realm.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realm.close();
                        }
                        Log.d(DescargasHelper.class.getName()+ "CLIENTES", mContentsArray.toString());
                    }
                    else {

                        RealmResults<Clientes> results = realm.where(Clientes.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArray.addAll(results);
                        }


                    }
                }

                @Override
                public void onFailure(Call<ClientesResponse> call, Throwable t) {
                }
            });
            // TODO descarga Bonuses
            Call<BonusesResponse> callBonuses = api.getBonusesTable(token);
            callBonuses.enqueue(new Callback<BonusesResponse>() {

                @Override
                public void onResponse(Call<BonusesResponse> callBonuses, Response<BonusesResponse> response) {
                    mContentsArrayBonuses.clear();
                    realmBonuses = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArrayBonuses.addAll(response.body().getBonuses());

                        try {


                            // Work with Realm
                            realmBonuses.beginTransaction();
                            realmBonuses.copyToRealmOrUpdate(mContentsArrayBonuses);
                            realmBonuses.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realmBonuses.close();
                        }
                        Log.d(DescargasHelper.class.getName()+"Bonuses", mContentsArrayBonuses.toString());
                    }
                    else {
                        RealmResults<Bonuses> results = realmBonuses.where(Bonuses.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayBonuses.addAll(results);
                        }

                    }
                }

                @Override
                public void onFailure(Call<BonusesResponse> callBonuses, Throwable t) {

                }
            });
            // TODO descarga Marcas
            Call<MarcasResponse> callMarcas = api.getMarcas(token);
            callMarcas.enqueue(new Callback<MarcasResponse>() {

                @Override
                public void onResponse(Call<MarcasResponse> callMarcas, Response<MarcasResponse> response) {
                    mContentsArrayMarcas.clear();
                    realmMarcas = Realm.getDefaultInstance();


                    if (response.isSuccessful()) {
                        mContentsArrayMarcas.addAll(response.body().getMarca());

                        try {

                            // Work with Realm
                            realmMarcas.beginTransaction();
                            realmMarcas.copyToRealmOrUpdate(mContentsArrayMarcas);
                            realmMarcas.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realmMarcas.close();
                        }
                        Log.d(DescargasHelper.class.getName()+"MARCAS", mContentsArrayMarcas.toString());
                    }
                    else {
                        RealmResults<Marcas> results = realmMarcas.where(Marcas.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayMarcas.addAll(results);
                        }

                    }
                }

                @Override
                public void onFailure(Call<MarcasResponse> callMarcas, Throwable t) {

                }
            });

            // TODO descarga Numeracion
            Call<NumeracionResponse> callNumeracion = api.getNumeracionDesc(token);
            callNumeracion.enqueue(new Callback<NumeracionResponse>() {

                @Override
                public void onResponse(Call<NumeracionResponse> callNumeracion, Response<NumeracionResponse> response) {
                    mContentsArrayNumeracion.clear();
                    realmNumeracion = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArrayNumeracion.addAll(response.body().getNumeracion());

                        try {


                            // Work with Realm
                            realmNumeracion.beginTransaction();
                            realmNumeracion.copyToRealmOrUpdate(mContentsArrayNumeracion);
                            realmNumeracion.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realmNumeracion.close();
                        }
                        Log.d(DescargasHelper.class.getName()+"Numeracion", mContentsArrayNumeracion.toString());

                    }
                    else {

                        RealmResults<Numeracion> results = realmNumeracion.where(Numeracion.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayNumeracion.addAll(results);
                        }


                    }
                }

                @Override
                public void onFailure(Call<NumeracionResponse> callNumeracion, Throwable t) {

                }
            });



// TODO descarga Usuarios
            Call<MetodoPagoResponse> callMetodoPago = api.getMetodoPago(token);
            callMetodoPago.enqueue(new Callback<MetodoPagoResponse>() {

                @Override
                public void onResponse(Call<MetodoPagoResponse> callMetodoPago, Response<MetodoPagoResponse> response) {
                    mContentsArrayMetodoPago.clear();
                    realmMetodoPago = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArrayMetodoPago.addAll(response.body().getMetodoPago());

                        try {


                            // Work with Realm
                            realmMetodoPago.beginTransaction();
                            realmMetodoPago.copyToRealmOrUpdate(mContentsArrayUsuarios);
                            realmMetodoPago.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realmMetodoPago.close();
                        }
                        Log.d(DescargasHelper.class.getName()+"USUARIOSRE", mContentsArrayMetodoPago.toString());
                    }
                    else {
                        RealmResults<MetodoPago> results = realmMetodoPago.where(MetodoPago.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayMetodoPago.addAll(results);
                        }

                    }
                }

                @Override
                public void onFailure(Call<MetodoPagoResponse> callMarcas, Throwable t) {

                }
            });



            // TODO descarga Tipo Productos
            Call<TipoProductoResponse> callTipoProducto = api.getTipoProducto(token);
            callTipoProducto.enqueue(new Callback<TipoProductoResponse>() {

                @Override
                public void onResponse(Call<TipoProductoResponse> callMarcas, Response<TipoProductoResponse> response) {
                    mContentsArrayTipoProducto.clear();
                    realmTipoProducto = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArrayTipoProducto.addAll(response.body().getTipoProducto());

                        try {


                            // Work with Realm
                            realmTipoProducto.beginTransaction();
                            realmTipoProducto.copyToRealmOrUpdate(mContentsArrayTipoProducto);
                            realmTipoProducto.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realmTipoProducto.close();
                        }
                        Log.d(DescargasHelper.class.getName()+"TIPOPROD", mContentsArrayTipoProducto.toString());

                    }
                    else {

                        RealmResults<TipoProducto> results = realmTipoProducto.where(TipoProducto.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayTipoProducto.addAll(results);
                        }

                    }
                }

                @Override
                public void onFailure(Call<TipoProductoResponse> callMarcas, Throwable t) {

                }
            });
            // TODO descarga Productos
            Call<ProductosResponse> call2 = api.getProducts(token);

            call2.enqueue(new Callback<ProductosResponse>() {

                @Override
                public void onResponse(Call<ProductosResponse> call2, Response<ProductosResponse> response2) {
                    mContentsArray2.clear();
                    realm2 = Realm.getDefaultInstance();
                    if (response2.isSuccessful()) {

                        mContentsArray2.addAll(response2.body().getProductos());

                        try {



                            realm2.beginTransaction();
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realm2.copyToRealmOrUpdate(mContentsArray2);
                            realm2.commitTransaction();
                        }
                        finally {
                            realm2.close();
                        }

                        Log.d(DescargasHelper.class.getName()+"PRODUCTOS", mContentsArray2.toString());

                    }
                    else {

                        RealmResults<Productos> results2 = realm2.where(Productos.class).findAll();
                        if (results2.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArray2.addAll(results2);
                        }


                    }
                    dialog.dismiss();

                }

                @Override
                public void onFailure(Call<ProductosResponse> call2, Throwable t) {
                    dialog.dismiss();


                }
            });
        }
        else {

        }
    }

    public void descargarInventario(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<Inventario> mContentsArray = new ArrayList<>();
        final ArrayList<invoice> mContentsArray2 = new ArrayList<>();
        final ArrayList<sale> mContentsArraySale = new ArrayList<>();
        final ArrayList<ProductoFactura> mContentsArrayPivot = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando lista de inventarios");

        if (isOnline()) {
            dialog.show();

            // TODO descarga Inventario
            Call<InventarioResponse> call = api.getInventory(token);

            call.enqueue(new Callback<InventarioResponse>() {

                @Override
                public void onResponse(Call<InventarioResponse> call, Response<InventarioResponse> response) {
                    mContentsArray.clear();
                    realm = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArray.addAll(response.body().getInventarios());

                        try {


                            // Work with Realm
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(mContentsArray);
                            realm.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realm.close();
                        }
                        Log.d(DescargasHelper.class.getName(), mContentsArray.toString());

                    }
                    else {

                        RealmResults<Inventario> results = realm.where(Inventario.class).findAll();
                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArray.addAll(results);
                        }


                    }
                }

                @Override
                public void onFailure(Call<InventarioResponse> call, Throwable t) {

                }
            });

            // TODO descarga invoice
            Call<FacturasResponse> call2 = api.getFacturas(token);

            call2.enqueue(new Callback<FacturasResponse>() {

                @Override
                public void onResponse(Call<FacturasResponse> call2, Response<FacturasResponse> response2) {
                    mContentsArray2.clear();
                    realm2 = Realm.getDefaultInstance();
                    if (response2.isSuccessful()) {


                       /* int id =response.body().getId();
                        String userName = response.body().getUsername();
                        String level = response.body().getLevel();
*/
                        mContentsArray2.addAll(response2.body().getFacturas());

                        try {

                            realm2.beginTransaction();
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realm2.copyToRealmOrUpdate(mContentsArray2);
                            realm2.commitTransaction();
                        }
                        finally {
                            realm2.close();
                        }

                       Log.d("finish", mContentsArray2 + " ");

                    }
                    else {
                        RealmResults<invoice> results2 = realm2.where(invoice.class).findAll();
                        if (results2.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArray2.addAll(results2);
                        }


                    }
                    dialog.dismiss();

                }

                @Override
                public void onFailure(Call<FacturasResponse> call2, Throwable t) {
                    dialog.dismiss();
                    RealmResults<invoice> results2 = realm2.where(invoice.class).findAll();
                    mContentsArray2.addAll(results2);
                }
            });
        }
        else {
        }

    }

    public void descargarDatosEmpresa(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<Sysconf> mContentsArraySys = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando datos de Empresa");

        if (isOnline()) {
            dialog.show();

            // TODO descarga DatosEmpresa
            Call<SysconfResponse> call2 = api.getSysconf(token);

            call2.enqueue(new Callback<SysconfResponse>() {

                @Override
                public void onResponse(Call<SysconfResponse> call2, Response<SysconfResponse> response2) {
                    mContentsArraySys.clear();
                    realmSysconfig = Realm.getDefaultInstance();
                    if (response2.isSuccessful()) {


                       /* int id =response.body().getId();
                        String userName = response.body().getUsername();
                        String level = response.body().getLevel();
*/
                        mContentsArraySys.addAll(response2.body().getSysconf());

                        try {

                            realmSysconfig.beginTransaction();
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realmSysconfig.copyToRealmOrUpdate(mContentsArraySys);
                            realmSysconfig.commitTransaction();
                        }
                        finally {
                            realmSysconfig.close();
                        }

                        Log.d("finishSysconf", mContentsArraySys + " ");

                    }
                    else {
                        RealmResults<Sysconf> results2 = realmSysconfig.where(Sysconf.class).findAll();

                        if (results2.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArraySys.addAll(results2);
                        }


                    }
                    dialog.dismiss();

                }

                @Override
                public void onFailure(Call<SysconfResponse> call2, Throwable t) {
                    dialog.dismiss();

                }
            });
        }
        else {

        }

    }


    public void descargarUsuarios(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<Usuarios> mContentsArrayUsuarios = new ArrayList<>();

            // TODO descarga Usuarios
            Call<UsuariosResponse> callusuarios = api.getUsuariosRetrofit(token);
            callusuarios.enqueue(new Callback<UsuariosResponse>() {

                @Override
                public void onResponse(Call<UsuariosResponse> callusuarios, Response<UsuariosResponse> response) {
                    mContentsArrayUsuarios.clear();
                    realmUsuarios = Realm.getDefaultInstance();

                    if (response.isSuccessful()) {
                        mContentsArrayUsuarios.addAll(response.body().getUsuarios());

                        try {


                            // Work with Realm
                            realmUsuarios.beginTransaction();
                            realmUsuarios.copyToRealmOrUpdate(mContentsArrayUsuarios);
                            realmUsuarios.commitTransaction();
                            //realm.close();
                        }
                        finally {
                            realmUsuarios.close();
                        }
                        Log.d("USUARIOSRE", mContentsArrayUsuarios.toString());
                    }
                    else {

                        RealmResults<Usuarios> results = realmUsuarios.where(Usuarios.class).findAll();

                        if (results.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayUsuarios.addAll(results);
                        }

                    }
                }

                @Override
                public void onFailure(Call<UsuariosResponse> callusuarios, Throwable t) {
                }
            });
        }

    public void descargarRecibos(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<recibos> mContentsArrayRecibos = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando recibos disponibles");

        if (isOnline()) {
            dialog.show();

            Call<RecibosResponse> call2 = api.getRecibos(token);

            call2.enqueue(new Callback<RecibosResponse>() {

                @Override
                public void onResponse(Call<RecibosResponse> call2, Response<RecibosResponse> response2) {
                    mContentsArrayRecibos.clear();
                    realmRecibos = Realm.getDefaultInstance();
                    if (response2.isSuccessful()) {


                       /* int id =response.body().getId();
                        String userName = response.body().getUsername();
                        String level = response.body().getLevel();
*/
                        mContentsArrayRecibos.addAll(response2.body().getRecibos());

                        try {

                            realmRecibos.beginTransaction();
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realmRecibos.copyToRealmOrUpdate(mContentsArrayRecibos);
                            realmRecibos.commitTransaction();
                        }
                        finally {
                            realmRecibos.close();
                        }

                        Log.d("descargaRecibos", mContentsArrayRecibos + " ");


                    }
                    else {

                        RealmResults<recibos> results2 = realmRecibos.where(recibos.class).findAll();

                        if (results2.isEmpty()){
                            Toast.makeText(mContext, "Error de descarga, contacte al administrador", Toast.LENGTH_SHORT).show();
                        }else{
                            mContentsArrayRecibos.addAll(results2);
                        }


                    }
                    dialog.dismiss();

                }

                @Override
                public void onFailure(Call<RecibosResponse> call2, Throwable t) {
                    dialog.dismiss();

                }
            });
        }
        else {

        }

    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
