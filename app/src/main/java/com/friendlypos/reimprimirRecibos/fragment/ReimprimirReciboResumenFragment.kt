package com.friendlypos.reimprimirRecibos.fragment

import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.friendlypos.application.util.Functions
import io.realm.Realm
import org.sufficientlysecure.htmltextview.HtmlTextView
import butterknife.BindView
import butterknife.ButterKnife

class ReimprimirReciboResumenFragment : BaseFragment() {
    var text: HtmlTextView? = null
    var recibo_actualizado: receipts? = null

    @BindView(R.id.btnReimprimirReciboNuevo)
    lateinit var btnReimprimirFactura: ImageButton
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    var activity: ReimprimirRecibosActivity? = null
    var facturaId: String? = ""
    var slecTAB: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(getContext())
        activity = ReimprimirRecibosActivity()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View =
            inflater.inflate(R.layout.fragment_reimprimir_recibo_resumen, container, false)
        text = rootView.findViewById<View>(R.id.html_textReciboNuevo) as HtmlTextView
        ButterKnife.bind(this, rootView)


        btnReimprimirFactura.setOnClickListener(View.OnClickListener {
            if (bluetoothStateChangeReceiver.isBluetoothAvailable == true) {
                val layoutInflater: LayoutInflater = LayoutInflater.from(getActivity())
                val promptView: View =
                    layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)

                val alertDialogBuilder = AlertDialog.Builder(getActivity())
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
                            val cantidadImpresiones: String = input.getText().toString()

                            PrinterFunctions.imprimirReimpRecibosTotal(
                                recibo_actualizado,
                                getActivity(),
                                1,
                                cantidadImpresiones
                            )
                            Toast.makeText(getActivity(), "Reimprimir recibo", Toast.LENGTH_SHORT)
                                .show()
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
        })

        return rootView
    }


    private val htmlPreview: Unit
        get() {
            try {
                val realm = Realm.getDefaultInstance()
                val clientes: Clientes = realm.where<Clientes>(Clientes::class.java)
                    .equalTo("id", recibo_actualizado.customer_id).findFirst()

                val nombreCliente: String = clientes.fantasyName
                Log.d("nombreCliente", nombreCliente)
                var preview = ""


                if (recibo_actualizado != null) {
                    preview += "<h5>" + "Recibos" + "</h5>"

                    preview += "<a><b>A nombre de:</b> $nombreCliente</a><br><br>"


                    /*   preview += "<a><b>" + padRight("# Referencia", 25) + padRight("# Factura", 25)+ "</b></a><br>";
       preview += "<a><b>" + padRight("Monto total", 25) + padRight("Monto Pagado", 25)+ "</b></a><br>";
       preview += "<a><b>" + padRight("Total en abonos", 35) + "</b></a><br>";
       preview += "<a><b>" + padRight("Total restante", 35) + "</b></a><br>";*/
                    preview += "<a>------------------------------------------------<a><br>"

                    preview += getPrintDistTotal(recibo_actualizado.customer_id)
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

    private fun getPrintDistTotal(idVenta: String): String {
        var send = ""

        val realm1 = Realm.getDefaultInstance()
        val result: RealmResults<receipts> =
            realm1.where<receipts>(receipts::class.java).equalTo("customer_id", idVenta)
                .equalTo("reference", recibo_actualizado.reference).findAll()
        Log.d("recibosresult", result.toString() + "")
        if (result.isEmpty()) {
            send = "No hay recibos emitidos"
        } else {
            val salesList1: List<receipts> =
                realm1.where<receipts>(receipts::class.java).equalTo("customer_id", idVenta)
                    .equalTo("reference", recibo_actualizado.reference).findAll()

            Log.d("getReference", salesList1[0].reference)

            val recibos: recibos = realm1.where<recibos>(recibos::class.java)
                .equalTo("numeration", salesList1[0].numeration).findFirst()

            Log.d("getNumeration", recibos.numeration + "")

            val numeroReferenciaReceipts: String = salesList1[0].reference
            val numeracionReceipts: String = salesList1[0].numeration
            val pagadoReceipts: Double = salesList1[0].montoCanceladoPorFactura
            val pagadoSReceipts = String.format("%,.2f", pagadoReceipts)

            val total: Double = recibos.total
            val totalS = String.format("%,.2f", total)

            val restante: Double = salesList1[0].porPagarReceipts
            val restanteS = String.format("%,.2f", restante)

            val totalAbonos: Double = salesList1[0].balance
            val totalAbonosS = String.format("%,.2f", totalAbonos)

            send +=  /*"# Referencia" + padRight(numeroReferenciaReceipts, 20) + "# Factura" + padRight(numeracionReceipts, 20)+ "<br>" +
                       "Monto total" + padRight(totalS, 40) + "Monto Pagado" + padRight(pagadoSReceipts, 40) + "<br>" +*/
                "<a><b>" + padRight("# Referencia:", 20.0) + "</b></a>" + padRight(
                    numeroReferenciaReceipts, 20.0
                ) + "<br>" +
                        "<a><b>" + padRight("# Factura:", 30.0) + "</b></a>" + padRight(
                    numeracionReceipts, 20.0
                ) + "<br>" +
                        "<a><b>" + padRight("Total Recibo:", 20.0) + "</b></a>" + padRight(
                    pagadoSReceipts,
                    20.0
                ) + "<br>" +
                        "<a><b>" + padRight("Monto total:", 30.0) + "</b></a>" + padRight(
                    totalS,
                    20.0
                ) + "<br>" +
                        "<a><b>" + padRight("Total en abonos:", 20.0) + "</b></a>" + padRight(
                    totalAbonosS,
                    20.0
                ) + "<br>" +
                        "<a><b>" + padRight("Total restante:", 25.0) + "</b></a>" + padRight(
                    restanteS,
                    20.0
                ) + "<br>"

            send += "<a>------------------------------------------------<a><br>"

            Log.d("FACTPRODTODFAC", send + "")


            realm1.close()
        }
        return send
    }

    override fun updateData() {
        slecTAB = (getActivity() as ReimprimirRecibosActivity).selecReciboTab
        if (slecTAB == 1) {
            facturaId = (getActivity() as ReimprimirRecibosActivity).invoiceIdReimprimirRecibo


            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                recibo_actualizado =
                    realm3.where<receipts>(receipts::class.java).equalTo("reference", facturaId)
                        .findFirst()
                realm3.close()
            }


            htmlPreview
            Log.d("FACTURAIDTOTALIZAR", recibo_actualizado.customer_id + "")
            Log.d("FACTURAIDTOTALIZAR", facturaId!!)
        } else {
            Log.d("nadaTotalizarupdate", "nadaTotalizarupdate")
        }
    }

    companion object {
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
    }
}


