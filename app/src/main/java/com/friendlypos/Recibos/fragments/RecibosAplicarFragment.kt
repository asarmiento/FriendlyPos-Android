package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.sync.SyncObjectServerFacade
import com.friendlysystemgroup.friendlypos.application.util.Functions
import com.friendlysystemgroup.friendlypos.application.util.LocalImageGetter
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions
import com.friendlysystemgroup.friendlypos.databinding.FragmentRecibosAplicarBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.util.GPSTracker
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.principal.modelo.datosTotales
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

class RecibosAplicarFragment : BaseFragment() {
    private var recibosActivity: RecibosActivity? = null
    private var pagado: Double = 0.0
    private var montoCancelado: Double = 0.0
    private var numeracion: String? = null
    private var gps: GPSTracker? = null
    private var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    private var session: SessionPrefes? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var slecTAB: Int = 0
    private var nextId: Int = 0
    private var facturaId: String? = null
    private var clienteId: String? = null
    private var receipts_ref: String? = null
    private var recibo_actualizado: recibos? = null
    private var observ: String? = null
    private var fecha: String? = null
    private var totalP: Double = 0.0
    private var totalDatosTotal2: Double = 0.0
    private var totalTotal: Double = 0.0
    private var datos_actualizados: datosTotales? = null
    
    // ViewBinding
    private var binding: FragmentRecibosAplicarBinding? = null
    private var apply_done = 0
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RecibosActivity) {
            recibosActivity = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        recibosActivity = null
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAll()
        pagado = 0.0
        activity?.unregisterReceiver(bluetoothStateChangeReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver?.setBluetoothStateChangeReceiver(context)
        session = SessionPrefes(SyncObjectServerFacade.getApplicationContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecibosAplicarBinding.inflate(inflater, container, false)
        return binding?.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
    }
    
    private fun setupListeners() {
        binding?.aplicarRecibo?.setOnClickListener {
            try {
                recibosActivity?.selecClienteTabRecibos = 0
                aplicarFactura()
            } catch (e: Exception) {
                context?.let {
                    Toast.makeText(it, e.message, Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
        }

        binding?.imprimirRecibo?.setOnClickListener {
            try {
                if (bluetoothStateChangeReceiver?.isBluetoothAvailable == true) {
                    showImprimirDialog()
                } else {
                    context?.let {
                        Functions.CreateMessage(
                            requireActivity(),
                            "Error",
                            "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                        )
                    }
                }
            } catch (e: Exception) {
                Functions.CreateMessage(
                    requireActivity(),
                    "Error",
                    "${e.message}\n${e.stackTrace.joinToString("\n")}"
                )
            }
        }
    }
    
    private fun showImprimirDialog() {
        val layoutInflater = LayoutInflater.from(requireActivity())
        val promptView = layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)

        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setView(promptView)
        
        val checkbox = promptView.findViewById<android.widget.CheckBox>(R.id.checkbox)
        val label = promptView.findViewById<android.widget.TextView>(R.id.promtClabelRecibosImp)
        label.text = "Escriba el número de impresiones requeridas"

        val input = promptView.findViewById<android.widget.EditText>(R.id.promtCtextRecibosImp)

        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            val cantidadImpresiones = input.text.toString()

            recibo_actualizado?.let { recibo ->
                PrinterFunctions.imprimirFacturaRecibosTotal(
                    recibo,
                    requireActivity(),
                    1,
                    cantidadImpresiones
                )
                context?.let {
                    Toast.makeText(it, "Imprimir liquidación", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        val alertD = alertDialogBuilder.create()
        alertD.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertD.show()
    }

    override fun updateData() {
        recibosActivity?.let { activity ->
            slecTAB = activity.selecClienteTabRecibos
            if (slecTAB == 1) {
                facturaId = activity.invoiceIdRecibos
                clienteId = activity.clienteIdRecibos
                totalP = activity.getTotalizarCancelado()

                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { r ->
                    recibo_actualizado = r.where(recibos::class.java)
                        .equalTo("customer_id", clienteId)
                        .findFirst()
                }
                realm.close()

                updateHtmlPreview()
                Log.d("FACTURAIDTOTALIZAR", clienteId ?: "")
            } else {
                Log.d("nadaTotalizarupdate", "nadaTotalizarupdate")
            }
        }
    }

    private fun updateHtmlPreview() {
        try {
            val realm = Realm.getDefaultInstance()
            
            recibo_actualizado?.let { recibo ->
                val clientes = realm.where(Clientes::class.java)
                    .equalTo("id", recibo.customer_id)
                    .findFirst()

                val nombreCliente = clientes?.fantasyName ?: ""
                var preview = ""

                preview += "<h5>Datos del recibo</h5>"
                preview += "<a><b>A nombre de:</b> $nombreCliente</a><br><br>"
                preview += getPrintDistTotal(recibo.customer_id)
                
                binding?.htmlTextRecibos?.setHtmlFromString(preview, LocalImageGetter())
            } ?: run {
                val preview = "<center><h2>Seleccione la factura a ver</h2></center>"
                binding?.htmlTextRecibos?.setHtmlFromString(preview, LocalImageGetter())
            }
            
            realm.close()
        } catch (e: Exception) {
            val preview = "<center><h2>Seleccione la factura a ver cath</h2></center>"
            binding?.htmlTextRecibos?.setHtmlFromString(preview, LocalImageGetter())
            Log.d("adsdad", e.message ?: "Unknown error")
        }
    }

    private fun actualizarFacturaDetalles() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { r ->
            val result: RealmResults<recibos> = r.where(recibos::class.java)
                .equalTo("customer_id", clienteId)
                .equalTo("abonado", 1)
                .findAll()
                
            if (result.isEmpty()) {
                context?.let {
                    Toast.makeText(it, "No hay recibos emitidos", Toast.LENGTH_LONG).show()
                }
            } else {
                for (i in result.indices) {
                    val salesList = r.where(recibos::class.java)
                        .equalTo("customer_id", clienteId)
                        .equalTo("abonado", 1)
                        .findAll()

                    val facturaId1 = salesList[i]?.invoice_id ?: continue
                    val recibo_actualizado = r.where(recibos::class.java)
                        .equalTo("invoice_id", facturaId1)
                        .findFirst() ?: continue
                        
                    recibo_actualizado.date = Functions.date
                    recibo_actualizado.observaciones = observ
                    recibo_actualizado.referencia_receipts = receipts_ref

                    r.insertOrUpdate(recibo_actualizado)
                    Log.d("ACT RECIBO", recibo_actualizado.toString())
                }
            }
        }
        realm.close()
    }

    private fun actualizarReceiptsDetalles() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { r ->
            val result: RealmResults<recibos> = r.where(recibos::class.java)
                .equalTo("customer_id", clienteId)
                .equalTo("abonado", 1)
                .findAll()
                
            if (result.isEmpty()) {
                context?.let {
                    Toast.makeText(it, "No hay recibos emitidos", Toast.LENGTH_LONG).show()
                }
            } else {
                for (i in result.indices) {
                    val salesList = r.where(recibos::class.java)
                        .equalTo("customer_id", clienteId)
                        .equalTo("abonado", 1)
                        .findAll()
                        
                    salesList[i]?.let { recibo ->
                        numeracion = recibo.numeration
                        pagado = recibo.paid
                        recibosActivity?.totalizarFinal = pagado

                        val receipts_id = recibosActivity?.receipts_id_num ?: ""
                        montoCancelado = recibo.montoCanceladoPorFactura
                        recibosActivity?.canceladoPorFactura = montoCancelado

                        val recibo_actua = r.where(receipts::class.java)
                            .equalTo("receipts_id", receipts_id)
                            .equalTo("customer_id", clienteId)
                            .findFirst() ?: return@let

                        receipts_ref = recibo_actua.reference
                        recibo_actua.listaRecibos = RealmList(*salesList.toTypedArray())
                        recibo_actua.balance = recibosActivity?.totalizarFinal ?: 0.0
                        recibo_actua.aplicado = 1
                        recibo_actua.sum = observ
                        recibo_actua.notes = observ
                        recibo_actua.montoPagado = montoCancelado
                        recibo_actua.numeration = numeracion
                        recibo_actua.montoCanceladoPorFactura = montoCancelado
                        recibo_actua.porPagarReceipts = restante
                        
                        r.insertOrUpdate(recibo_actua)
                        Log.d("ACTRECIBO", recibo_actua.toString())
                    }
                }
                pagado = 0.0
            }
        }
        realm.close()
    }

    private fun actualizarDatosTotales() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()
        val currentIdNum: Number? = realm.where(datosTotales::class.java).max("id")
        nextId = currentIdNum?.toInt()?.plus(1) ?: 1

        val datos_actualizados = datosTotales()
        datos_actualizados.id = nextId
        datos_actualizados.idTotal = 5
        datos_actualizados.nombreTotal = "Recibo"
        datos_actualizados.totalRecibos = recibosActivity?.canceladoPorFactura ?: 0.0
        datos_actualizados.date = Functions.date

        realm.copyToRealmOrUpdate(datos_actualizados)
        realm.commitTransaction()
        Log.d("datosTotalesRec", datos_actualizados.toString())
        realm.close()
    }

    private fun aplicarFactura() {
        val observacionesText = binding?.txtRecibosObservaciones?.text.toString()
        
        if (observacionesText.isNotEmpty()) {
            observ = observacionesText
            actualizarReceiptsDetalles()
            actualizarFacturaDetalles()
            actualizarDatosTotales()
            
            context?.let {
                Toast.makeText(it, "Recibo realizado correctamente", Toast.LENGTH_LONG).show()
            }

            binding?.aplicarRecibo?.visibility = View.GONE
            binding?.imprimirRecibo?.visibility = View.VISIBLE
            apply_done = 1
        } else {
            binding?.txtRecibosObservaciones?.error = "Campo requerido"
            binding?.txtRecibosObservaciones?.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        var pagadoMostrarS: String? = null
        var restante: Double = 0.0
        var restanteS: String? = null
        
        fun newInstance(): RecibosAplicarFragment = RecibosAplicarFragment()
        
        fun padRight(s: String, n: Double): String {
            val centeredString: String
            val pad = (n + 4) - s.length

            if (pad > 0) {
                val pd = Functions.paddigTabs((pad / 2.0).toInt().toLong())
                centeredString = "\t" + s + "\t" + pd
                println("pad: |$centeredString|")
            } else {
                centeredString = "\t" + s + "\t"
            }
            return centeredString
        }

        private fun getPrintDistTotal(idVenta: String?): String {
            if (idVenta == null) return "No hay recibos emitidos"
            
            var send = ""
            val realm = Realm.getDefaultInstance()
            
            try {
                val result: RealmResults<recibos> = realm.where(recibos::class.java)
                    .equalTo("customer_id", idVenta)
                    .equalTo("abonado", 1)
                    .equalTo("mostrar", 1)
                    .findAll()

                if (result.isEmpty()) {
                    send = "No hay recibos emitidos"
                } else {
                    for (i in result.indices) {
                        val salesList = realm.where(recibos::class.java)
                            .equalTo("customer_id", idVenta)
                            .equalTo("abonado", 1)
                            .equalTo("mostrar", 1)
                            .findAll()

                        salesList[i]?.let { recibo ->
                            val numeracion = recibo.numeration ?: ""
                            val total = recibo.total
                            val totalS = String.format("%,.2f", total)

                            val pagado = recibo.montoCanceladoPorFactura
                            val pagadoS = String.format("%,.2f", pagado)

                            restante = recibo.porPagar
                            restanteS = String.format("%,.2f", restante)

                            val totalAbonos = recibo.paid
                            val totalAbonosS = String.format("%,.2f", totalAbonos)

                            send += "<a><b>" + padRight("# Factura:", 30.0) + "</b></a>" + 
                                    padRight(numeracion, 20.0) + "<br>" +
                                    "<a><b>" + padRight("Total Recibo:", 20.0) + "</b></a>" + 
                                    padRight(pagadoS, 20.0) + "<br>" +
                                    "<a><b>" + padRight("Monto total:", 30.0) + "</b></a>" + 
                                    padRight(totalS, 20.0) + "<br>" +
                                    "<a><b>" + padRight("Total en abonos:", 20.0) + "</b></a>" + 
                                    padRight(totalAbonosS, 20.0) + "<br>" +
                                    "<a><b>" + padRight("Total restante:", 25.0) + "</b></a>" + 
                                    padRight(restanteS ?: "0.00", 20.0) + "<br>"

                            send += "<a>------------------------------------------------<a><br>"
                            Log.d("FACTPRODTODFAC", send)
                        }
                    }
                }
            } finally {
                realm.close()
            }
            
            return send
        }

        fun clearAll() {
            if (apply_done == 1) {
                apply_done = 0
            }
            try {
                System.gc()
            } catch (e: Exception) {
                // No action needed
            }
        }
    }
}
