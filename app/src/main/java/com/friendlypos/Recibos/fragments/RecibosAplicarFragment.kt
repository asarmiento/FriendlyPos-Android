package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
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
import org.sufficientlysecure.htmltextview.HtmlTextView

class RecibosAplicarFragment : BaseFragment() {
    var activity: RecibosActivity? = null
    var pagado: Double = 0.0
    var montoCancelado: Double = 0.0
    var numeracion: String? = null
    var gps: GPSTracker? = null
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    var session: SessionPrefes? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var slecTAB: Int = 0
    var nextId: Int = 0
    var txtFecha: EditText? = null
    var facturaId: String? = null
    var clienteId: String? = null
    var receipts_ref: String? = null
    var recibo_actualizado: recibos? = null
    var text: HtmlTextView? = null
    var observ: String? = null
    var fecha: String? = null
    var totalP: Double = 0.0
    var totalDatosTotal2: Double = 0.0
    var totalTotal: Double = 0.0
    var datos_actualizados: datosTotales? = null
    
    // ViewBinding
    private var _binding: FragmentRecibosAplicarBinding? = null
    private val binding get() = _binding!!
    
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as RecibosActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAll()
        pagado = 0.0
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver)
        _binding = null
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(activity)
        session = SessionPrefes(SyncObjectServerFacade.getApplicationContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecibosAplicarBinding.inflate(inflater, container, false)

        observaciones = binding.txtRecibosObservaciones
        applyBill = binding.aplicarRecibo
        printBill = binding.imprimirRecibo
        text = binding.htmlTextRecibos
        
        applyBill!!.setOnClickListener {
            try {
                val tabCliente = 0
                (getActivity() as RecibosActivity).selecClienteTabRecibos = tabCliente
                aplicarFactura()
                //  actualizarRecibo();
            } catch (e: Exception) {
                Toast.makeText(getActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }


        printBill!!.setOnClickListener {
            try {
                if (bluetoothStateChangeReceiver.isBluetoothAvailable == true) {
                    val layoutInflater: LayoutInflater = LayoutInflater.from(getActivity())
                    val promptView: View =
                        layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)

                    val alertDialogBuilder =
                        AlertDialog.Builder(getActivity())
                    alertDialogBuilder.setView(promptView)
                    val checkbox: CheckBox =
                        promptView.findViewById<View>(R.id.checkbox) as CheckBox

                    val label: TextView =
                        promptView.findViewById<View>(R.id.promtClabelRecibosImp) as TextView
                    label.setText("Escriba el número de impresiones requeridas")

                    val input: EditText =
                        promptView.findViewById<View>(R.id.promtCtextRecibosImp) as EditText

                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                val cantidadImpresiones: String =
                                    input.getText().toString()

                                PrinterFunctions.imprimirFacturaRecibosTotal(
                                    recibo_actualizado,
                                    getActivity(),
                                    1,
                                    cantidadImpresiones
                                )
                                Toast.makeText(
                                    getActivity(),
                                    "imprimir liquidacion",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    alertDialogBuilder.setNegativeButton(
                        "Cancel",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                dialog.cancel()
                            }
                        })

                    val alertD = alertDialogBuilder.create()
                    alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    alertD.show()
                } else if (bluetoothStateChangeReceiver.isBluetoothAvailable == false) {
                    Functions.CreateMessage(
                        getActivity(),
                        "Error",
                        "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                    )
                }
            } catch (e: Exception) {
                Functions.CreateMessage(
                    getActivity(),
                    "Error",
                    """
                        ${e.message}
                        ${e.stackTrace}
                        """.trimIndent()
                )
            }
        }
        return binding.root
    }

    override fun updateData() {
        slecTAB = activity.selecClienteTabRecibos
        if (slecTAB == 1) {
            facturaId = activity.invoiceIdRecibos
            clienteId = activity.clienteIdRecibos
            totalP = activity.getTotalizarCancelado()

            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                recibo_actualizado =
                    realm3.where<recibos>(recibos::class.java).equalTo("customer_id", clienteId)
                        .findFirst()
                realm3.close()
            }


            htmlPreview

            Log.d("FACTURAIDTOTALIZAR", clienteId!!)
        } else {
            Log.d("nadaTotalizarupdate", "nadaTotalizarupdate")
        }
    }

    private val htmlPreview: Unit
        get() {
            try {
                val realm = Realm.getDefaultInstance()
                val clientes: Clientes = realm.where<Clientes>(Clientes::class.java)
                    .equalTo("id", recibo_actualizado.getCustomer_id()).findFirst()

                val nombreCliente: String = clientes.fantasyName

                var preview = ""


                if (recibo_actualizado != null) {
                    preview += "<h5>" + "Datos del recibo" + "</h5>"

                    preview += "<a><b>A nombre de:</b> $nombreCliente</a><br><br>"

                    /*    preview += "<a><b>" + padRight("# Factura", 35) + padRight("Monto Pagado", 35)+ "</b></a><br>";
      preview += "<a><b>" + padRight("Monto total", 35)+ "</b></a><br>";
      preview += "<a><b>" + padRight("Total en abonos", 35) + "</b></a><br>";
      preview += "<a><b>" + padRight("Total restante", 35) + "</b></a><br>";
      preview += "<a>------------------------------------------------<a><br>";*/
                    preview += getPrintDistTotal(recibo_actualizado.getCustomer_id())
                } else {
                    preview += "<center><h2>Seleccione la factura a ver</h2></center>"
                }
                text.setHtmlFromString(preview, LocalImageGetter())
            } catch (e: Exception) {
                val preview =
                    "<center><h2>Seleccione la factura a ver cath</h2></center>"
                text.setHtmlFromString(preview, LocalImageGetter())
                Log.d("adsdad", e.message!!)
            }
        }

    fun actualizarFacturaDetalles() {
        val realm2 = Realm.getDefaultInstance()
        realm2.executeTransaction { realm2 ->
            val result: RealmResults<recibos> =
                realm2.where<recibos>(recibos::class.java).equalTo("customer_id", clienteId)
                    .equalTo("abonado", 1).findAll()
            if (result.isEmpty()) {
                Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show()
            } else {
                for (i in result.indices) {
                    val salesList1: List<recibos> = realm2.where<recibos>(
                        recibos::class.java
                    ).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll()

                    val facturaId1: String = salesList1[i].getInvoice_id()

                    val recibo_actualizado: recibos =
                        realm2.where<recibos>(recibos::class.java).equalTo("invoice_id", facturaId1)
                            .findFirst()
                    //   recibo_actualizado.setMostrar(0);
                    recibo_actualizado.setDate(Functions.date)
                    recibo_actualizado.setObservaciones(observ)
                    recibo_actualizado.setReferencia_receipts(receipts_ref)

                    realm2.insertOrUpdate(recibo_actualizado)

                    Log.d("ACT RECIBO", recibo_actualizado.toString() + "")
                }
                realm2.close()
            }
        }
    }


    fun actualizarReceiptsDetalles() {
        val realm2 = Realm.getDefaultInstance()
        realm2.executeTransaction { realm2 ->
            val result: RealmResults<recibos> =
                realm2.where<recibos>(recibos::class.java).equalTo("customer_id", clienteId)
                    .equalTo("abonado", 1).findAll()
            if (result.isEmpty()) {
                Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show()
            } else {
                for (i in result.indices) {
                    val salesList1: List<recibos> = realm2.where<recibos>(
                        recibos::class.java
                    ).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll()
                    numeracion = salesList1[i].getNumeration()
                    pagado = salesList1[i].getPaid()
                    activity.totalizarFinal = pagado

                    val receipts_id: String = activity.receipts_id_num
                    montoCancelado = salesList1[i].getMontoCanceladoPorFactura()
                    activity.canceladoPorFactura = montoCancelado


                    val recibo_actua: receipts = realm2.where<receipts>(receipts::class.java)
                        .equalTo("receipts_id", receipts_id).equalTo("customer_id", clienteId)
                        .findFirst()



                    receipts_ref = recibo_actua.getReference()
                    recibo_actua.setListaRecibos(RealmList<recibos>(*salesList1.toTypedArray<recibos>()))
                    recibo_actua.setBalance(activity.totalizarFinal)
                    recibo_actua.setAplicado(1)
                    recibo_actua.setSum(observ)
                    recibo_actua.setNotes(observ)
                    recibo_actua.setMontoPagado(montoCancelado)
                    recibo_actua.setNumeration(numeracion)
                    recibo_actua.setMontoCanceladoPorFactura(montoCancelado)
                    recibo_actua.setPorPagarReceipts(restante)
                    realm2.insertOrUpdate(recibo_actua)

                    Log.d("ACTRECIBO", recibo_actua.toString() + "")
                }
                realm2.close()
                pagado = 0.0
            }
        }
    }

    protected fun actualizarDatosTotales() {
        val realm5 = Realm.getDefaultInstance()

        realm5.beginTransaction()
        val currentIdNum: Number = realm5.where<datosTotales>(datosTotales::class.java).max("id")

        nextId = if (currentIdNum == null) {
            1
        } else {
            currentIdNum.toInt() + 1
        }


        val datos_actualizados: datosTotales = datosTotales()

        datos_actualizados.id = nextId
        datos_actualizados.idTotal = 5
        datos_actualizados.nombreTotal = "Recibo"
        datos_actualizados.totalRecibos = activity.canceladoPorFactura
        datos_actualizados.date = Functions.date

        realm5.copyToRealmOrUpdate<datosTotales>(datos_actualizados)
        realm5.commitTransaction()
        Log.d("datosTotalesRec", datos_actualizados.toString() + "")
        realm5.close()
    }


    protected fun aplicarFactura() {
        //  fecha = txtFecha.getText().toString();

        observaciones.getText().toString()

        if (!observaciones.getText().toString().isEmpty()) {
            observ = observaciones.getText().toString()
            actualizarReceiptsDetalles()
            actualizarFacturaDetalles()

            actualizarDatosTotales()
            Toast.makeText(getActivity(), "Recibo realizado correctamente", Toast.LENGTH_LONG)
                .show()

            applyBill!!.visibility = View.GONE
            printBill!!.visibility = View.VISIBLE
            apply_done = 1
        } else {
            observaciones.setError("Campo requerido")
            observaciones.requestFocus()
        }
    }

    companion object {
        private var applyBill: Button? = null
        private var printBill: Button? = null
        private var apply_done = 0
        var pagadoMostrarS: String? = null
        private var observaciones: EditText? = null
        var restante: Double = 0.0
        var restanteS: String? = null
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

        private fun getPrintDistTotal(idVenta: String): String {
            var send = ""

            val realm1 = Realm.getDefaultInstance()
            val result: RealmResults<recibos> =
                realm1.where<recibos>(recibos::class.java).equalTo("customer_id", idVenta)
                    .equalTo("abonado", 1).equalTo("mostrar", 1).findAll()

            if (result.isEmpty()) {
                send = "No hay recibos emitidos"
            } else {
                for (i in result.indices) {
                    val salesList1: List<recibos> =
                        realm1.where<recibos>(recibos::class.java).equalTo("customer_id", idVenta)
                            .equalTo("abonado", 1).equalTo("mostrar", 1).findAll()


                    val numeracion: String = salesList1[i].getNumeration()
                    val total: Double = salesList1[i].getTotal()
                    val totalS = String.format("%,.2f", total)

                    val pagado: Double = salesList1[i].getMontoCanceladoPorFactura()
                    val pagadoS = String.format("%,.2f", pagado)

                    restante = salesList1[i].getPorPagar()
                    restanteS = String.format("%,.2f", restante)

                    val totalAbonos: Double = salesList1[i].getPaid()
                    val totalAbonosS = String.format("%,.2f", totalAbonos)


                    send += "<a><b>" + padRight("# Factura:", 30.0) + "</b></a>" + padRight(
                        numeracion,
                        20.0
                    ) + "<br>" +
                            "<a><b>" + padRight("Total Recibo:", 20.0) + "</b></a>" + padRight(
                        pagadoS,
                        20.0
                    ) + "<br>" +
                            "<a><b>" + padRight(
                        "Monto total:",
                        30.0
                    ) + "</b></a>" + padRight(totalS, 20.0) + "<br>" +
                            "<a><b>" + padRight("Total en abonos:", 20.0) + "</b></a>" + padRight(
                        totalAbonosS,
                        20.0
                    ) + "<br>" +
                            "<a><b>" + padRight("Total restante:", 25.0) + "</b></a>" + padRight(
                        restanteS!!, 20.0
                    ) + "<br>"

                    send += "<a>------------------------------------------------<a><br>"

                    Log.d("FACTPRODTODFAC", send + "")
                }
                realm1.close()
            }
            return send
        }


        fun clearAll() {
            if (apply_done == 1) {
                apply_done = 0
                // paid.getText().clear();
            }
            try {
                System.gc()
            } catch (e: Exception) {
            }
        }
    }
}
