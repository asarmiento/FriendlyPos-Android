package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;

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
    private Realm realm, realm2, realmMarcas, realmTipoProducto;

    public DescargasHelper(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        realm = Realm.getDefaultInstance();
        realm2 = Realm.getDefaultInstance();
        realmMarcas = Realm.getDefaultInstance();
        realmTipoProducto = Realm.getDefaultInstance();
    }

    public void descargarCatalogo(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<Clientes> mContentsArray = new ArrayList<>();
        final ArrayList<Productos> mContentsArray2 = new ArrayList<>();
        final ArrayList<Marcas> mContentsArrayMarcas = new ArrayList<>();
        final ArrayList<TipoProducto> mContentsArrayTipoProducto = new ArrayList<>();
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


                    if (response.isSuccessful()) {
                        mContentsArray.addAll(response.body().getContents());

                        try {
                            realm = Realm.getDefaultInstance();

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
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                        mContentsArray.addAll(results);
                    }
                }

                @Override
                public void onFailure(Call<ClientesResponse> call, Throwable t) {
                    // Toast.makeText(context, getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            });

            // TODO descarga Marcas
            Call<MarcasResponse> callMarcas = api.getMarcas(token);
            callMarcas.enqueue(new Callback<MarcasResponse>() {

                @Override
                public void onResponse(Call<MarcasResponse> callMarcas, Response<MarcasResponse> response) {
                    mContentsArrayMarcas.clear();


                    if (response.isSuccessful()) {
                        mContentsArrayMarcas.addAll(response.body().getMarca());

                        try {
                            realmMarcas = Realm.getDefaultInstance();

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
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<Marcas> results = realmMarcas.where(Marcas.class).findAll();
                        mContentsArrayMarcas.addAll(results);
                    }
                }

                @Override
                public void onFailure(Call<MarcasResponse> callMarcas, Throwable t) {
                    // Toast.makeText(context, getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            });

            // TODO descarga Tipo Productos
            Call<TipoProductoResponse> callTipoProducto = api.getTipoProducto(token);
            callTipoProducto.enqueue(new Callback<TipoProductoResponse>() {

                @Override
                public void onResponse(Call<TipoProductoResponse> callMarcas, Response<TipoProductoResponse> response) {
                    mContentsArrayTipoProducto.clear();


                    if (response.isSuccessful()) {
                        mContentsArrayTipoProducto.addAll(response.body().getTipoProducto());

                        try {
                            realmTipoProducto = Realm.getDefaultInstance();

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
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<TipoProducto> results = realmTipoProducto.where(TipoProducto.class).findAll();
                        mContentsArrayTipoProducto.addAll(results);
                    }
                }

                @Override
                public void onFailure(Call<TipoProductoResponse> callMarcas, Throwable t) {
                    // Toast.makeText(context, getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            });
            // TODO descarga Productos
            Call<ProductosResponse> call2 = api.getProducts(token);

            call2.enqueue(new Callback<ProductosResponse>() {

                @Override
                public void onResponse(Call<ProductosResponse> call2, Response<ProductosResponse> response2) {
                    mContentsArray2.clear();

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

                        //Toast.makeText(ProductosActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Toast.makeText(ProductosActivity.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<Productos> results2 = realm2.where(Productos.class).findAll();
                        mContentsArray2.addAll(results2);
                    }
                    dialog.dismiss();

                }

                @Override
                public void onFailure(Call<ProductosResponse> call2, Throwable t) {
                    dialog.dismiss();

                    //  Toast.makeText(ProductosActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();

                }
            });
        }
        else {
            //     Toast.makeText(context, getString(R.string.failed), Toast.LENGTH_LONG).show();
        }
    }

    public void descargarInventario(Context context) {
        String token = "Bearer " + SessionPrefes.get(context).getToken();
        Log.d("tokenCliente", token + " ");

        final RequestInterface api = BaseManager.getApi();
        final ArrayList<Inventario> mContentsArray = new ArrayList<>();
        final ArrayList<Facturas> mContentsArray2 = new ArrayList<>();
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


                    if (response.isSuccessful()) {
                        mContentsArray.addAll(response.body().getInventarios());

                        try {
                            realm = Realm.getDefaultInstance();

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
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //  Toast.makeText(DescargarInventario.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<Inventario> results = realm.where(Inventario.class).findAll();
                        mContentsArray.addAll(results);
                    }
                }

                @Override
                public void onFailure(Call<InventarioResponse> call, Throwable t) {
                    // Toast.makeText(context, getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            });

            // TODO descarga Facturas
            Call<FacturasResponse> call2 = api.getFacturas(token);

            call2.enqueue(new Callback<FacturasResponse>() {

                @Override
                public void onResponse(Call<FacturasResponse> call2, Response<FacturasResponse> response2) {
                    mContentsArray2.clear();

                    if (response2.isSuccessful()) {

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

                        //Toast.makeText(ProductosActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Toast.makeText(ProductosActivity.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<Facturas> results2 = realm2.where(Facturas.class).findAll();
                        mContentsArray2.addAll(results2);
                    }
                    dialog.dismiss();

                }

                @Override
                public void onFailure(Call<FacturasResponse> call2, Throwable t) {
                    dialog.dismiss();

                    //  Toast.makeText(ProductosActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                    RealmResults<Facturas> results2 = realm2.where(Facturas.class).findAll();
                    mContentsArray2.addAll(results2);
                }
            });
        }
        else {
            //     Toast.makeText(context, getString(R.string.failed), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
