package com.friendlysystemgroup.friendlypos.reimpresion_pedidos.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.application.util.Functions.date
import com.friendlysystemgroup.friendlypos.application.util.Functions.get24Time
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions.imprimirFacturaPrevTotal
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions.imprimirFacturaProformaTotal
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.distribucion.util.GPSTracker
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.activity.ReimprimirPedidosActivity
import io.realm.Realm
import io.realm.RealmList
import io.realm.internal.SyncObjectServerFacade

class ReimPedidoTotalizarFragment : BaseFragment() {
    //  private static EditText paid;
    var totalGrabado: Double = 0.0
    var totalExento: Double = 0.0
    var totalSubtotal: Double = 0.0
    var totalDescuento: Double = 0.0
    var totalImpuesto: Double = 0.0
    var totalTotal: Double = 0.0
    var totalVuelvo: String = "0"
    var totalPagoCon: String = "0"
    var facturaId: String? = null
    var usuer: String? = null
    var session: SessionPrefes? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var metodoPagoCliente: String? = null
    var activity: ReimprimirPedidosActivity? = null
    var slecTAB: Int = 0
    var sale_actualizada: sale? = null
    var tipoFacturacionImpr: String? = null
    var gps: GPSTracker? = null
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAll()
        requireActivity().unregisterReceiver(bluetoothStateChangeReceiver)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as ReimprimirPedidosActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver?.setBluetoothStateChangeReceiver(requireContext())
        session = SessionPrefes(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_reim_pedido_totalizar, container, false)


        client_name = rootView.findViewById<View>(R.id.client_name) as EditText

        subExe = rootView.findViewById<View>(R.id.subExento) as TextView
        subGra = rootView.findViewById<View>(R.id.subGrabado) as TextView
        ivaSub = rootView.findViewById<View>(R.id.IvaFact) as TextView
        subT = rootView.findViewById<View>(R.id.subTotal) as TextView
        discount = rootView.findViewById<View>(R.id.Discount) as TextView
        Total = rootView.findViewById<View>(R.id.Total) as TextView

        //paid = (EditText) rootView.findViewById(R.id.txtPaid);

        // change = (TextView) rootView.findViewById(R.id.txtChange);
        slecTAB = (requireActivity() as ReimprimirPedidosActivity).selecClienteTab

        if (slecTAB == 1) {
            metodoPagoCliente = (requireActivity() as ReimprimirPedidosActivity).metodoPagoCliente


            if (metodoPagoCliente == "1") {
                //bill_type = 1;
                try {
                    Toast.makeText(requireActivity(), "1", Toast.LENGTH_LONG).show()

                    //paid.setEnabled(true);
                } catch (e: Exception) {
                    Log.d("JD", "Error " + e.message)
                }
            } else if (metodoPagoCliente == "2") {
                Toast.makeText(requireActivity(), "2", Toast.LENGTH_LONG).show()
                //  bill_type = 2;
                // paid.setEnabled(false);
            }
        } else {
            Toast.makeText(requireActivity(), "nadaTotalizar", Toast.LENGTH_LONG).show()
        }
        notes = rootView.findViewById<View>(R.id.txtNotes) as EditText

        applyBill = rootView.findViewById<View>(R.id.applyInvoice) as Button
        printBill = rootView.findViewById<View>(R.id.printInvoice) as Button

        if (apply_done == 1) {
            applyBill?.visibility = View.GONE
            printBill?.visibility = View.VISIBLE
        } else {
            applyBill?.visibility = View.VISIBLE
            printBill?.visibility = View.GONE
        }

        applyBill?.setOnClickListener {
            try {
                //validateData();
                // Log.d("total", String.valueOf(Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString())));

                if (metodoPagoCliente == "1") {
                    /* pagoCon = Double.parseDouble(paid.getText().toString());
                                    totalPagoCon = String.format("%,.2f", pagoCon);
                                    double total = totalTotal;
    
                                    if (pagoCon >= total) {
                                        vuelto = pagoCon - total;
                                        totalVuelvo = String.format("%,.2f", vuelto);*/

                    val tabCliente = 0
                    (requireActivity() as ReimprimirPedidosActivity).selecClienteTab = tabCliente

                    //    change.setText(totalVuelvo);
                    obtenerLocalización()
                    aplicarFactura()
                } else if (metodoPagoCliente == "2") {
                    Toast.makeText(requireActivity(), "Crédito", Toast.LENGTH_LONG).show()
                    obtenerLocalización()
                    aplicarFactura()
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        printBill?.setOnClickListener {
            try {
                if (bluetoothStateChangeReceiver?.isBluetoothAvailable == true) {
                    val realm3 = Realm.getDefaultInstance()
                    realm3.executeTransaction { realm3 ->
                        sale_actualizada = realm3.where(sale::class.java)
                            .equalTo("invoice_id", facturaId).findFirst()
                        Log.d("ENVIADOSALE", sale_actualizada.toString() + "")
                    }

                    tipoFacturacionImpr = sale_actualizada?.facturaDePreventa

                    if (tipoFacturacionImpr == "Preventa") {
                        imprimirFacturaPrevTotal(
                            sale_actualizada,
                            requireActivity(), 1
                        )
                        Toast.makeText(
                            requireActivity(),
                            "imprimir Totalizar Preventa",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (tipoFacturacionImpr == "Proforma") {
                        imprimirFacturaProformaTotal(
                            sale_actualizada,
                            requireActivity(), 1
                        )
                        Toast.makeText(
                            requireActivity(),
                            "imprimir Totalizar Preventa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (bluetoothStateChangeReceiver?.isBluetoothAvailable == false) {
                    CreateMessage(
                        requireActivity(),
                        "Error",
                        "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                    )
                }
            } catch (e: Exception) {
                CreateMessage(
                    requireActivity(),
                    "Error",
                    """
                        ${e.message}
                        ${e.stackTrace}
                        """.trimIndent()
                )
            }
        }


        return rootView
    }

    override fun updateData() {
        if (slecTAB == 1) {
            //   paid.getText().clear();

            totalGrabado = (requireActivity() as ReimprimirPedidosActivity).getTotalizarSubGrabado()
            totalExento = (requireActivity() as ReimprimirPedidosActivity).getTotalizarSubExento()
            totalSubtotal = (requireActivity() as ReimprimirPedidosActivity).getTotalizarSubTotal()
            totalDescuento = (requireActivity() as ReimprimirPedidosActivity).getTotalizarDescuento()
            totalImpuesto = (requireActivity() as ReimprimirPedidosActivity).getTotalizarImpuestoIVA()
            totalTotal = (requireActivity() as ReimprimirPedidosActivity).getTotalizarTotal()
            facturaId = (requireActivity() as ReimprimirPedidosActivity).invoiceId.toString()

            subGra?.text =
                String.format("%,.2f", totalGrabado)
            subExe?.text =
                String.format("%,.2f", totalExento)

            subT?.text =
                String.format("%,.2f", totalSubtotal)
            discount?.text =
                String.format("%,.2f", totalDescuento)

            ivaSub?.text =
                String.format("%,.2f", totalImpuesto)
            Total?.text =
                String.format("%,.2f", totalTotal)

            Log.d("FACTURAIDTOTALIZAR", facturaId!!)
        } else {
            Toast.makeText(requireActivity(), "nadaTotalizarupdate", Toast.LENGTH_LONG).show()
        }
    }

    fun obtenerLocalización() {
        gps = GPSTracker(requireActivity())

        // check if GPS enabled
        if (gps?.canGetLocation() == true) {
            latitude = gps?.getLatitude() ?: 0.0
            longitude = gps?.getLongitude() ?: 0.0

            /* messageTextView2.setText("Mi direccion es: \n"
                    + latitude + "log "  + longitude );
            // \n is for new line
            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();*/
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps?.showSettingsAlert()
        }
    }

    protected fun actualizarFactura() {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA FACTURAS

        val realm2 = Realm.getDefaultInstance()
        realm2.executeTransaction { realm2 ->
            val factura_actualizada =
                realm2.where(invoice::class.java).equalTo("id", facturaId).findFirst()
            val realm = Realm.getDefaultInstance()
            usuer = session?.usuarioPrefs
            val usuarios =
                realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
            val idUsuario = usuarios?.id
            realm.close()

            factura_actualizada?.date = date
            factura_actualizada?.times = get24Time()

            factura_actualizada?.latitud = latitude
            factura_actualizada?.longitud = longitude

            factura_actualizada?.subtotal_taxed = totalGrabado.toString()
            factura_actualizada?.subtotal_exempt = totalExento.toString()
            factura_actualizada?.subtotal = totalSubtotal.toString()
            factura_actualizada?.discount = totalDescuento.toString()
            factura_actualizada?.tax = totalImpuesto.toString()
            factura_actualizada?.total = totalTotal.toString()

            factura_actualizada?.paid = pagoCon.toString()
            factura_actualizada?.changing = vuelto.toString()
            factura_actualizada?.user_id_applied = idUsuario
            factura_actualizada?.note =
                notes?.text.toString() ?: ""
            factura_actualizada?.canceled = "1"
            factura_actualizada?.aplicada = 1
            factura_actualizada?.subida = 1

            val result = realm.where(Pivot::class.java)
                .equalTo("invoice_id", facturaId) /*.equalTo("devuelvo", 0)*/.findAll()
            Log.d("FACTURANUEVA", result.toString() + "")


            val results = RealmList<Pivot>()

            results.addAll(result.subList(0, result.size))
            factura_actualizada?.productofactura = results

            Log.d("CREAR DISTRIBUCION", factura_actualizada.toString() + "")


            realm2.insertOrUpdate(factura_actualizada)
            realm2.close()
        }
    }

    protected fun actualizarVenta() {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()
        realm3.executeTransaction { realm3 ->
            sale_actualizada =
                realm3.where(sale::class.java).equalTo("invoice_id", facturaId).findFirst()
            Log.d(
                "dadasdad",
                client_name?.text.toString() ?: ""
            )

            val nombreEscrito = client_name?.text.toString() ?: ""

            if (nombreEscrito.matches("".toRegex())) {
                Toast.makeText(requireActivity(), "Nombre no cambio", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "Nombre cambio", Toast.LENGTH_SHORT).show()
                sale_actualizada?.customer_name =
                    client_name?.text.toString() ?: ""
            }

            sale_actualizada?.sale_type = "1"
            sale_actualizada?.applied = "1"
            sale_actualizada?.updated_at =
                date + " " + get24Time()
            sale_actualizada?.aplicada = 1
            sale_actualizada?.subida = 1

            realm3.insertOrUpdate(sale_actualizada)
            realm3.close()
            Log.d("ENVIADOSALE", sale_actualizada.toString() + "")
        }
    }

    protected fun aplicarFactura() {
        // paid.setEnabled(false);

        actualizarFactura()
        actualizarVenta()

        Toast.makeText(requireActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show()

        applyBill?.visibility = View.GONE
        printBill?.visibility = View.VISIBLE
        apply_done = 1
    }

    companion object {
        private var subGra: TextView? = null
        private var subExe: TextView? = null
        private var subT: TextView? = null
        private var ivaSub: TextView? = null
        private var discount: TextView? = null
        private var Total: TextView? = null


        //  private static TextView change;
        private var notes: EditText? = null
        private var client_name: EditText? = null

        var pagoCon: Double = 0.0
        var vuelto: Double = 0.0
        private var applyBill: Button? = null
        private var printBill: Button? = null
        private var apply_done = 0
        fun clearAll() {
            if (apply_done == 1) {
                apply_done = 0
                //   paid.getText().clear();
            }
            try {
                System.gc()
            } catch (e: Exception) {
            }
        }
    }
}