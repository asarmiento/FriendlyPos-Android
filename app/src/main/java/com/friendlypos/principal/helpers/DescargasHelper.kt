package com.friendlypos.principal.helpers

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.friendlypos.Recibos.modelo.RecibosResponse
import com.friendlypos.Recibos.modelo.recibos
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.application.interfaces.RequestInterface.getRecibos
import com.friendlypos.application.interfaces.RequestInterface.getSysconf
import com.friendlypos.distribucion.modelo.FacturasResponse
import com.friendlypos.distribucion.modelo.Inventario
import com.friendlypos.distribucion.modelo.InventarioResponse
import com.friendlypos.distribucion.modelo.Marcas
import com.friendlypos.distribucion.modelo.MarcasResponse
import com.friendlypos.distribucion.modelo.MetodoPago
import com.friendlypos.distribucion.modelo.MetodoPagoResponse
import com.friendlypos.distribucion.modelo.ProductoFactura
import com.friendlypos.distribucion.modelo.TipoProducto
import com.friendlypos.distribucion.modelo.TipoProductoResponse
import com.friendlypos.distribucion.modelo.invoice
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.login.modelo.Usuarios
import com.friendlypos.login.modelo.UsuariosResponse
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.preventas.modelo.Bonuses
import com.friendlypos.preventas.modelo.BonusesResponse
import com.friendlypos.preventas.modelo.Numeracion
import com.friendlypos.preventas.modelo.NumeracionResponse
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.ClientesResponse
import com.friendlypos.principal.modelo.ConsecutivosNumberFe
import com.friendlypos.principal.modelo.ConsecutivosNumberFeResponse
import com.friendlypos.principal.modelo.Productos
import com.friendlypos.principal.modelo.ProductosResponse
import com.friendlypos.principal.modelo.Sysconf
import com.friendlypos.principal.modelo.SysconfResponse
import com.friendlypos.principal.modelo.datosTotales
import io.realm.Realm

/**
 * Created by DelvoM on 03/11/2017.
 */
class DescargasHelper(private val activity: Activity) {
    private val networkStateChangeReceiver =
        NetworkStateChangeReceiver()

    private val mContext: Context = activity
    private var realm: Realm? = null
    private var realm2: Realm? = null
    private var realmSysconfig: Realm? = null
    private var realmConsecutivo: Realm? = null
    private var realmRecibos: Realm? = null
    private var realmMarcas: Realm? = null
    private var realmNumeracion: Realm? = null
    private var realmBonuses: Realm? = null
    private var realmTipoProducto: Realm? = null
    private var realmUsuarios: Realm? = null
    private var realmMetodoPago: Realm? = null
    var nextId: Int = 0
    var nombre: String? = null

    fun descargarCatalogo(context: Context) {
        val token = "Bearer " + get(context).token
        Log.d("tokenCliente", "$token ")

        val api: RequestInterface? = api
        val mContentsArray = ArrayList<Clientes>()
        val mContentsArray2 = ArrayList<Productos>()
        val mContentsArrayMarcas = ArrayList<Marcas>()
        val mContentsArrayNumeracion = ArrayList<Numeracion>()
        val mContentsArrayBonuses = ArrayList<Bonuses>()
        val mContentsArrayTipoProducto = ArrayList<TipoProducto>()
        val mContentsArrayMetodoPago = ArrayList<MetodoPago>()
        val mContentsArrayUsuarios = ArrayList<Usuarios>()
        val dialog = ProgressDialog(context)
        dialog.setMessage("Cargando lista de catálogo")

        if (isOnline) {
            dialog.show()

            // TODO descarga Clientes
            val call: retrofit2.Call<ClientesResponse> = api!!.getJSON(token)

            call.enqueue(object : retrofit2.Callback<ClientesResponse?> {
                override fun onResponse(
                    call: retrofit2.Call<ClientesResponse?>?,
                    response: retrofit2.Response<ClientesResponse?>
                ) {
                    mContentsArray.clear()
                    realm = Realm.getDefaultInstance()

                    if (response.isSuccessful()) {
                        mContentsArray.addAll(response.body().getContents())

                        try {
                            // Work with Realm


                            realm.beginTransaction()
                            realm.copyToRealmOrUpdate(mContentsArray)
                            realm.commitTransaction()
                            //realm.close();
                        } finally {
                            realm.close()
                        }
                        Log.d(
                            DescargasHelper::class.java.name + "CLIENTES",
                            mContentsArray.toString()
                        )
                    } else {
                        val results = realm.where(Clientes::class.java).findAll()

                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArray.addAll(results)
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<ClientesResponse?>?, t: Throwable?) {
                }
            })
            // TODO descarga Bonuses
            val callBonuses: retrofit2.Call<BonusesResponse> = api.getBonusesTable(token)
            callBonuses.enqueue(object : retrofit2.Callback<BonusesResponse?> {
                override fun onResponse(
                    callBonuses: retrofit2.Call<BonusesResponse?>?,
                    response: retrofit2.Response<BonusesResponse?>
                ) {
                    mContentsArrayBonuses.clear()
                    realmBonuses = Realm.getDefaultInstance()

                    if (response.isSuccessful()) {
                        mContentsArrayBonuses.addAll(response.body().bonuses)

                        try {
                            // Work with Realm


                            realmBonuses.beginTransaction()
                            realmBonuses.copyToRealmOrUpdate(mContentsArrayBonuses)
                            realmBonuses.commitTransaction()
                            //realm.close();
                        } finally {
                            realmBonuses.close()
                        }
                        Log.d(
                            DescargasHelper::class.java.name + "Bonuses",
                            mContentsArrayBonuses.toString()
                        )
                    } else {
                        val results = realmBonuses.where(Bonuses::class.java).findAll()

                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayBonuses.addAll(results)
                        }
                    }
                }

                override fun onFailure(
                    callBonuses: retrofit2.Call<BonusesResponse?>?,
                    t: Throwable?
                ) {
                }
            })
            // TODO descarga Marcas
            val callMarcas: retrofit2.Call<MarcasResponse> = api.getMarcas(token)
            callMarcas.enqueue(object : retrofit2.Callback<MarcasResponse?> {
                override fun onResponse(
                    callMarcas: retrofit2.Call<MarcasResponse?>?,
                    response: retrofit2.Response<MarcasResponse?>
                ) {
                    mContentsArrayMarcas.clear()
                    realmMarcas = Realm.getDefaultInstance()


                    if (response.isSuccessful()) {
                        mContentsArrayMarcas.addAll(response.body().marca)

                        try {
                            // Work with Realm

                            realmMarcas.beginTransaction()
                            realmMarcas.copyToRealmOrUpdate(mContentsArrayMarcas)
                            realmMarcas.commitTransaction()
                            //realm.close();
                        } finally {
                            realmMarcas.close()
                        }
                        Log.d(
                            DescargasHelper::class.java.name + "MARCAS",
                            mContentsArrayMarcas.toString()
                        )
                    } else {
                        val results = realmMarcas.where(Marcas::class.java).findAll()

                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayMarcas.addAll(results)
                        }
                    }
                }

                override fun onFailure(
                    callMarcas: retrofit2.Call<MarcasResponse?>?,
                    t: Throwable?
                ) {
                }
            })

            // TODO descarga Numeracion
            val callNumeracion: retrofit2.Call<NumeracionResponse> = api.getNumeracionDesc(token)
            callNumeracion.enqueue(object : retrofit2.Callback<NumeracionResponse?> {
                override fun onResponse(
                    callNumeracion: retrofit2.Call<NumeracionResponse?>?,
                    response: retrofit2.Response<NumeracionResponse?>
                ) {
                    mContentsArrayNumeracion.clear()
                    realmNumeracion = Realm.getDefaultInstance()

                    if (response.isSuccessful()) {
                        mContentsArrayNumeracion.addAll(response.body().numeracion)

                        try {
                            // Work with Realm


                            realmNumeracion.beginTransaction()
                            realmNumeracion.copyToRealmOrUpdate(mContentsArrayNumeracion)
                            realmNumeracion.commitTransaction()
                            //realm.close();
                        } finally {
                            realmNumeracion.close()
                        }
                        Log.d(
                            DescargasHelper::class.java.name + "Numeracion",
                            mContentsArrayNumeracion.toString()
                        )
                    } else {
                        val results = realmNumeracion.where(
                            Numeracion::class.java
                        ).findAll()

                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayNumeracion.addAll(results)
                        }
                    }
                }

                override fun onFailure(
                    callNumeracion: retrofit2.Call<NumeracionResponse?>?,
                    t: Throwable?
                ) {
                }
            })


            // TODO descarga Usuarios
            val callMetodoPago: retrofit2.Call<MetodoPagoResponse> = api.getMetodoPago(token)
            callMetodoPago.enqueue(object : retrofit2.Callback<MetodoPagoResponse?> {
                override fun onResponse(
                    callMetodoPago: retrofit2.Call<MetodoPagoResponse?>?,
                    response: retrofit2.Response<MetodoPagoResponse?>
                ) {
                    mContentsArrayMetodoPago.clear()
                    realmMetodoPago = Realm.getDefaultInstance()

                    if (response.isSuccessful()) {
                        mContentsArrayMetodoPago.addAll(response.body().metodoPago)

                        try {
                            // Work with Realm


                            realmMetodoPago.beginTransaction()
                            realmMetodoPago.copyToRealmOrUpdate(mContentsArrayUsuarios)
                            realmMetodoPago.commitTransaction()
                            //realm.close();
                        } finally {
                            realmMetodoPago.close()
                        }
                        Log.d(
                            DescargasHelper::class.java.name + "USUARIOSRE",
                            mContentsArrayMetodoPago.toString()
                        )
                    } else {
                        val results = realmMetodoPago.where(
                            MetodoPago::class.java
                        ).findAll()

                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayMetodoPago.addAll(results)
                        }
                    }
                }

                override fun onFailure(
                    callMarcas: retrofit2.Call<MetodoPagoResponse?>?,
                    t: Throwable?
                ) {
                }
            })


            // TODO descarga Tipo Productos
            val callTipoProducto: retrofit2.Call<TipoProductoResponse> = api.getTipoProducto(token)
            callTipoProducto.enqueue(object : retrofit2.Callback<TipoProductoResponse?> {
                override fun onResponse(
                    callMarcas: retrofit2.Call<TipoProductoResponse?>?,
                    response: retrofit2.Response<TipoProductoResponse?>
                ) {
                    mContentsArrayTipoProducto.clear()
                    realmTipoProducto = Realm.getDefaultInstance()

                    if (response.isSuccessful()) {
                        mContentsArrayTipoProducto.addAll(response.body().tipoProducto)

                        try {
                            // Work with Realm


                            realmTipoProducto.beginTransaction()
                            realmTipoProducto.copyToRealmOrUpdate(mContentsArrayTipoProducto)
                            realmTipoProducto.commitTransaction()
                            //realm.close();
                        } finally {
                            realmTipoProducto.close()
                        }
                        Log.d(
                            DescargasHelper::class.java.name + "TIPOPROD",
                            mContentsArrayTipoProducto.toString()
                        )
                    } else {
                        val results = realmTipoProducto.where(
                            TipoProducto::class.java
                        ).findAll()

                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayTipoProducto.addAll(results)
                        }
                    }
                }

                override fun onFailure(
                    callMarcas: retrofit2.Call<TipoProductoResponse?>?,
                    t: Throwable?
                ) {
                }
            })
            // TODO descarga Productos
            val call2: retrofit2.Call<ProductosResponse> = api.getProducts(token)

            call2.enqueue(object : retrofit2.Callback<ProductosResponse?> {
                override fun onResponse(
                    call2: retrofit2.Call<ProductosResponse?>?,
                    response2: retrofit2.Response<ProductosResponse?>
                ) {
                    mContentsArray2.clear()
                    realm2 = Realm.getDefaultInstance()
                    if (response2.isSuccessful()) {
                        mContentsArray2.addAll(response2.body().getProductos())

                        try {
                            realm2.beginTransaction()
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realm2.copyToRealmOrUpdate(mContentsArray2)
                            realm2.commitTransaction()
                        } finally {
                            realm2.close()
                        }

                        Log.d(
                            DescargasHelper::class.java.name + "PRODUCTOS",
                            mContentsArray2.toString()
                        )
                    } else {
                        val results2 = realm2.where(Productos::class.java).findAll()
                        if (results2.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArray2.addAll(results2)
                        }
                    }
                    dialog.dismiss()
                }

                override fun onFailure(call2: retrofit2.Call<ProductosResponse?>?, t: Throwable?) {
                    dialog.dismiss()
                }
            })
        } else {
        }
    }

    fun descargarInventario(context: Context) {
        val token = "Bearer " + get(context).token
        Log.d("tokenCliente", "$token ")

        val api: RequestInterface? = api
        val mContentsArray = ArrayList<Inventario>()
        val mContentsArray2 = ArrayList<invoice>()
        val mContentsArraySale = ArrayList<sale>()
        val mContentsArrayPivot = ArrayList<ProductoFactura>()
        val dialog = ProgressDialog(context)
        dialog.setMessage("Cargando lista de inventarios")

        if (isOnline) {
            dialog.show()

            // TODO descarga Inventario
            val call: retrofit2.Call<InventarioResponse> = api!!.getInventory(token)

            call.enqueue(object : retrofit2.Callback<InventarioResponse?> {
                override fun onResponse(
                    call: retrofit2.Call<InventarioResponse?>?,
                    response: retrofit2.Response<InventarioResponse?>
                ) {
                    mContentsArray.clear()
                    realm = Realm.getDefaultInstance()

                    if (response.isSuccessful()) {
                        mContentsArray.addAll(response.body().inventarios)

                        try {
                            // Work with Realm


                            realm.beginTransaction()
                            realm.copyToRealmOrUpdate(mContentsArray)
                            realm.commitTransaction()
                            //realm.close();
                        } finally {
                            realm.close()
                        }
                        Log.d(DescargasHelper::class.java.name, mContentsArray.toString())
                    } else {
                        val results = realm.where(Inventario::class.java).findAll()
                        if (results.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArray.addAll(results)
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<InventarioResponse?>?, t: Throwable?) {
                }
            })

            // TODO descarga invoice
            val call2: retrofit2.Call<FacturasResponse> = api.getFacturas(token)

            call2.enqueue(object : retrofit2.Callback<FacturasResponse?> {
                override fun onResponse(
                    call2: retrofit2.Call<FacturasResponse?>?,
                    response2: retrofit2.Response<FacturasResponse?>
                ) {
                    mContentsArray2.clear()
                    realm2 = Realm.getDefaultInstance()
                    if (response2.isSuccessful()) {
                        /* int id =response.body().getId();
                                               String userName = response.body().getUsername();
                                               String level = response.body().getLevel();
                       */


                        mContentsArray2.addAll(response2.body().facturas)

                        try {
                            realm2.beginTransaction()
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realm2.copyToRealmOrUpdate(mContentsArray2)
                            realm2.commitTransaction()
                        } finally {
                            realm2.close()
                        }

                        Log.d("finish", "$mContentsArray2 ")
                    } else {
                        val results2 = realm2.where(invoice::class.java).findAll()
                        if (results2.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArray2.addAll(results2)
                        }
                    }
                    dialog.dismiss()
                }

                override fun onFailure(call2: retrofit2.Call<FacturasResponse?>?, t: Throwable?) {
                    dialog.dismiss()
                    val results2 = realm2!!.where(invoice::class.java).findAll()
                    mContentsArray2.addAll(results2)
                }
            })
        } else {
        }
    }

    fun descargarDatosEmpresa(context: Context) {
        val token = "Bearer " + get(context).token
        Log.d("tokenCliente", "$token ")

        val api: RequestInterface? = api
        val mContentsArraySys = ArrayList<Sysconf>()
        val mContentsArrayConsecutivo = ArrayList<ConsecutivosNumberFe>()
        val dialog = ProgressDialog(context)
        dialog.setMessage("Cargando datos de Empresa")

        if (isOnline) {
            dialog.show()

            // TODO descarga DatosEmpresa
            val call2: retrofit2.Call<SysconfResponse> = api!!.getSysconf(token)

            call2.enqueue(object : retrofit2.Callback<SysconfResponse?> {
                override fun onResponse(
                    call2: retrofit2.Call<SysconfResponse?>?,
                    response2: retrofit2.Response<SysconfResponse?>
                ) {
                    mContentsArraySys.clear()
                    realmSysconfig = Realm.getDefaultInstance()
                    if (response2.isSuccessful()) {
                        /* int id =response.body().getId();
                                               String userName = response.body().getUsername();
                                               String level = response.body().getLevel();
                       */


                        mContentsArraySys.addAll(response2.body().getSysconf())

                        try {
                            realmSysconfig.beginTransaction()
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realmSysconfig.copyToRealmOrUpdate(mContentsArraySys)
                            realmSysconfig.commitTransaction()
                        } finally {
                            realmSysconfig.close()
                        }

                        Log.d("finishSysconf", "$mContentsArraySys ")
                    } else {
                        val results2 = realmSysconfig.where(Sysconf::class.java).findAll()

                        if (results2.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArraySys.addAll(results2)
                        }
                    }
                }

                override fun onFailure(call2: retrofit2.Call<SysconfResponse?>?, t: Throwable?) {
                }
            })


            val callCons: retrofit2.Call<ConsecutivosNumberFeResponse> =
                api.getConsecutivosNumber(token)

            callCons.enqueue(object : retrofit2.Callback<ConsecutivosNumberFeResponse?> {
                override fun onResponse(
                    callCons: retrofit2.Call<ConsecutivosNumberFeResponse?>?,
                    response2: retrofit2.Response<ConsecutivosNumberFeResponse?>
                ) {
                    mContentsArrayConsecutivo.clear()
                    realmConsecutivo = Realm.getDefaultInstance()
                    if (response2.isSuccessful()) {
                        /* int id =response.body().getId();
                                               String userName = response.body().getUsername();
                                               String level = response.body().getLevel();
                       */


                        mContentsArrayConsecutivo.addAll(response2.body().getConsecutivosNumberFe())

                        try {
                            realmConsecutivo.beginTransaction()
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realmConsecutivo.copyToRealmOrUpdate(mContentsArrayConsecutivo)
                            realmConsecutivo.commitTransaction()
                        } finally {
                            realmConsecutivo.close()
                        }

                        Log.d("finishSysconf", "$mContentsArrayConsecutivo ")
                    } else {
                        val results2 = realmConsecutivo.where(
                            ConsecutivosNumberFe::class.java
                        ).findAll()

                        if (results2.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayConsecutivo.addAll(results2)
                        }
                    }
                    dialog.dismiss()
                }

                override fun onFailure(
                    call2: retrofit2.Call<ConsecutivosNumberFeResponse?>?,
                    t: Throwable?
                ) {
                    dialog.dismiss()
                }
            })
        } else {
        }


        val realm5 = Realm.getDefaultInstance()
        for (i in 0..4) {
            if (i == 0) {
                nombre = "Distribucion"
            } else if (i == 1) {
                nombre = "VentaDirecta"
            } else if (i == 2) {
                nombre = "Preventa"
            } else if (i == 3) {
                nombre = "Proforma"
            } else if (i == 4) {
                nombre = "Recibo"
            }

            realm5.beginTransaction()

            val currentIdNum = realm5.where(datosTotales::class.java).max("idTotal")

            nextId = if (currentIdNum == null) {
                1
            } else {
                currentIdNum.toInt() + 1
            }

            val datos = datosTotales()

            datos.idTotal = nextId
            datos.nombreTotal = nombre

            realm5.insertOrUpdate(datos)
            realm5.commitTransaction()
            Log.d("datosTotales", datos.toString() + "")
        }
        realm5.close()
    }


    fun descargarUsuarios(context: Context) {
        val token = "Bearer " + get(context).token
        Log.d("tokenCliente", "$token ")

        val api: RequestInterface? = api
        val mContentsArrayUsuarios = ArrayList<Usuarios>()

        // TODO descarga Usuarios
        val callusuarios: retrofit2.Call<UsuariosResponse> = api!!.getUsuariosRetrofit(token)
        callusuarios.enqueue(object : retrofit2.Callback<UsuariosResponse?> {
            override fun onResponse(
                callusuarios: retrofit2.Call<UsuariosResponse?>?,
                response: retrofit2.Response<UsuariosResponse?>
            ) {
                mContentsArrayUsuarios.clear()
                realmUsuarios = Realm.getDefaultInstance()

                if (response.isSuccessful()) {
                    mContentsArrayUsuarios.addAll(response.body().usuarios)

                    try {
                        // Work with Realm


                        realmUsuarios.beginTransaction()
                        realmUsuarios.copyToRealmOrUpdate(mContentsArrayUsuarios)
                        realmUsuarios.commitTransaction()
                        //realm.close();
                    } finally {
                        realmUsuarios.close()
                    }
                    Log.d("USUARIOSRE", mContentsArrayUsuarios.toString())
                } else {
                    val results = realmUsuarios.where(Usuarios::class.java).findAll()

                    if (results.isEmpty()) {
                        Toast.makeText(
                            mContext,
                            "Error de descarga, contacte al administrador",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        mContentsArrayUsuarios.addAll(results)
                    }
                }
            }

            override fun onFailure(
                callusuarios: retrofit2.Call<UsuariosResponse?>?,
                t: Throwable?
            ) {
            }
        })
    }

    fun descargarRecibos(context: Context) {
        val token = "Bearer " + get(context).token
        Log.d("tokenCliente", "$token ")

        val api: RequestInterface? = api
        val mContentsArrayRecibos = ArrayList<recibos>()
        val dialog = ProgressDialog(context)
        dialog.setMessage("Cargando recibos disponibles")

        if (isOnline) {
            dialog.show()

            val call2: retrofit2.Call<RecibosResponse> = api!!.getRecibos(token)

            call2.enqueue(object : retrofit2.Callback<RecibosResponse?> {
                override fun onResponse(
                    call2: retrofit2.Call<RecibosResponse?>?,
                    response2: retrofit2.Response<RecibosResponse?>
                ) {
                    mContentsArrayRecibos.clear()
                    realmRecibos = Realm.getDefaultInstance()
                    if (response2.isSuccessful()) {
                        /* int id =response.body().getId();
                                               String userName = response.body().getUsername();
                                               String level = response.body().getLevel();
                       */


                        mContentsArrayRecibos.addAll(response2.body().getRecibos())

                        try {
                            realmRecibos.beginTransaction()
                            //TODO verificar cada cuanto se va a actualizar el inventario.
                            //realm.copyToRealm(mContentsArray2);
                            realmRecibos.copyToRealmOrUpdate(mContentsArrayRecibos)
                            realmRecibos.commitTransaction()
                        } finally {
                            realmRecibos.close()
                        }

                        Log.d("descargaRecibos", "$mContentsArrayRecibos ")
                    } else {
                        val results2 = realmRecibos.where(recibos::class.java).findAll()

                        if (results2.isEmpty()) {
                            Toast.makeText(
                                mContext,
                                "Error de descarga, contacte al administrador",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mContentsArrayRecibos.addAll(results2)
                        }
                    }
                    dialog.dismiss()
                }

                override fun onFailure(call2: retrofit2.Call<RecibosResponse?>?, t: Throwable?) {
                    dialog.dismiss()
                }
            })
        } else {
        }
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(mContext)
}
