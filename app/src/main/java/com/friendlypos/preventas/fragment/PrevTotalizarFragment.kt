package com.friendlypos.preventas.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.friendlypos.R
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlypos.application.util.Functions.date
import com.friendlypos.application.util.Functions.get24Time
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.distribucion.util.GPSTracker
import com.friendlypos.login.modelo.Usuarios
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.preventas.activity.PreventaActivity
import com.friendlypos.preventas.modelo.Numeracion
import com.friendlypos.principal.activity.MenuPrincipal
import com.friendlypos.principal.modelo.datosTotales
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade

class PrevTotalizarFragment : BaseFragment() {
    // private static TextView change;
    var num_actualizada: Numeracion? = null

    // private static EditText paid;
    var totalGrabado: Double = 0.0
    var totalExento: Double = 0.0
    var totalSubtotal: Double = 0.0
    var totalDescuento: Double = 0.0
    var totalImpuesto: Double = 0.0
    var totalTotal: Double = 0.0
    var facturaId: String? = null
    var usuer: String? = null
    var session: SessionPrefes? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var metodoPagoCliente: String? = null
    var slecTAB: Int = 0
    var sale_actualizada: sale? = null
    var activity: PreventaActivity? = null
    var nextId: Int = 0
    var gps: GPSTracker? = null
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    var tipoFacturacion: String? = null
    var tipoFacturacionImpr: String? = null
    var datos_actualizados: datosTotales? = null
    var totalDatosTotal: Double = 0.0
    var totalDatosTotal2: Double = 0.0
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as PreventaActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAll()
        getActivity()!!.unregisterReceiver(bluetoothStateChangeReceiver)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver!!.setBluetoothStateChangeReceiver(activity!!)
        session = SessionPrefes(SyncObjectServerFacade.getApplicationContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_prev_totalizar, container, false)

        client_name = rootView.findViewById<View>(R.id.client_name) as EditText

        subExe = rootView.findViewById<View>(R.id.subExento) as TextView
        subGra = rootView.findViewById<View>(R.id.subGrabado) as TextView
        ivaSub = rootView.findViewById<View>(R.id.IvaFact) as TextView
        subT = rootView.findViewById<View>(R.id.subTotal) as TextView
        discount = rootView.findViewById<View>(R.id.Discount) as TextView
        Total = rootView.findViewById<View>(R.id.Total) as TextView

        //  paid = (EditText) rootView.findViewById(R.id.txtPaid);

        //   change = (TextView) rootView.findViewById(R.id.txtChange);
        slecTAB = (getActivity() as PreventaActivity).selecClienteTabPreventa
        if (slecTAB == 1) {
            metodoPagoCliente = (getActivity() as PreventaActivity).metodoPagoClientePreventa


            if (metodoPagoCliente == "1") {
                //bill_type = 1;
                try {
                    Log.d("Pago", "1 Contado")
                } catch (e: Exception) {
                    Log.d("JD", "Error " + e.message)
                }
            } else if (metodoPagoCliente == "2") {
                Log.d("Pago", "2 Credito")
                //  bill_type = 2;
                //    paid.setEnabled(false);
            }
        } else {
            Log.d("nadaTotalizar", "nadaTotalizar")
        }
        notes = rootView.findViewById<View>(R.id.txtNotes) as EditText

        applyBill = rootView.findViewById<View>(R.id.applyInvoice) as Button

        //  printBill = (Button) rootView.findViewById(R.id.printInvoice);
        if (apply_done == 1) {
            applyBill!!.visibility = View.VISIBLE
            // printBill.setVisibility(View.VISIBLE);
        } else {
            applyBill!!.visibility = View.VISIBLE
            // printBill.setVisibility(View.GONE);
        }

        applyBill!!.setOnClickListener {
            try {
                if (metodoPagoCliente == "1") {
                    val tabCliente = 0
                    (getActivity() as PreventaActivity).selecClienteTabPreventa = tabCliente

                    //     Toast.makeText(getActivity(), "Contado", Toast.LENGTH_LONG).show();
                    obtenerLocalización()
                    aplicarFactura()
                } else if (metodoPagoCliente == "2") {
                    val tabCliente = 0
                    (getActivity() as PreventaActivity).selecClienteTabPreventa = tabCliente
                    //  Toast.makeText(getActivity(), "Crédito", Toast.LENGTH_LONG).show();
                    obtenerLocalización()
                    aplicarFactura()
                }
                actualizarFactura()
                actualizarNumeracion()
            } catch (e: Exception) {
                Toast.makeText(getActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
            handler = Handler()
            runnable = Runnable {
                val intent = Intent(getActivity(), MenuPrincipal::class.java)
                getActivity()!!.startActivity(intent)
            }


            handler!!.removeCallbacks(runnable)
            handler!!.postDelayed(
                runnable,
                500
            )
        }


        /*
        printBill.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {

                        if (bluetoothStateChangeReceiver.isBluetoothAvailable() == true) {

                            sale ventaDetallePreventa = activity.getCurrentVenta();
                            ventaDetallePreventa.getInvoice_id();
                            tipoFacturacionImpr = ventaDetallePreventa.getFacturaDePreventa();

                            if(tipoFacturacionImpr.equals("Preventa")){
                                PrinterFunctions.imprimirFacturaPrevTotal(sale_actualizada, getActivity(), 1);

                            }
                            else if(tipoFacturacionImpr.equals("Proforma")){
                                PrinterFunctions.imprimirFacturaProformaTotal(sale_actualizada, getActivity(), 1);

                            }
                            clearAll();

                        }
                        else if (bluetoothStateChangeReceiver.isBluetoothAvailable() == false) {
                            Functions.CreateMessage(getActivity(), "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                        }


                    }
                    catch (Exception e) {
                        Functions.CreateMessage(getActivity(), "Error", e.getMessage() + "\n" + e.getStackTrace().toString());
                    }
                }
            }

        );*/
        return rootView
    }

    override fun updateData() {
        if (slecTAB == 1) {
            //  paid.getText().clear();

            totalGrabado = (getActivity() as PreventaActivity).getTotalizarSubGrabado()
            totalExento = (getActivity() as PreventaActivity).getTotalizarSubExento()
            totalSubtotal = (getActivity() as PreventaActivity).getTotalizarSubTotal()
            totalDescuento = (getActivity() as PreventaActivity).getTotalizarDescuento()
            totalImpuesto = (getActivity() as PreventaActivity).getTotalizarImpuestoIVA()
            totalTotal = (getActivity() as PreventaActivity).getTotalizarTotal()
            facturaId = (getActivity() as PreventaActivity).invoiceIdPreventa.toString()

            subGra!!.text =
                String.format("%,.2f", totalGrabado)
            subExe!!.text =
                String.format("%,.2f", totalExento)

            subT!!.text =
                String.format("%,.2f", totalSubtotal)
            discount!!.text =
                String.format("%,.2f", totalDescuento)

            ivaSub!!.text =
                String.format("%,.2f", totalImpuesto)
            Total!!.text = String.format("%,.2f", totalTotal)

            Log.d("FACTURAIDTOTALIZAR", facturaId!!)
        } else {
            Log.d("nadaTotalizarupdate", "nadaTotalizarupdate")
        }
    }

    fun obtenerLocalización() {
        gps = GPSTracker(getActivity()!!)

        if (gps!!.canGetLocation()) {
            latitude = gps!!.getLatitude()
            longitude = gps!!.getLongitude()
        } else {
            gps!!.showSettingsAlert()
        }
    }

    fun actualizarFacturaDetalles() {
        val realm = Realm.getDefaultInstance()
        usuer = session!!.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        val idUsuario = usuarios!!.id
        realm.close()

        val ventaDetallePreventa = activity!!.currentVenta
        ventaDetallePreventa.invoice_id
        tipoFacturacion = ventaDetallePreventa.facturaDePreventa

        val invoiceDetallePreventa1 = activity!!.currentInvoice
        invoiceDetallePreventa1.p_longitud = longitude
        invoiceDetallePreventa1.p_latitud = latitude

        invoiceDetallePreventa1.p_subtotal = totalSubtotal.toString()
        invoiceDetallePreventa1.p_subtotal_taxed = totalGrabado.toString()
        invoiceDetallePreventa1.p_subtotal_exempt = totalExento.toString()
        invoiceDetallePreventa1.p_discount = totalDescuento.toString()
        invoiceDetallePreventa1.p_tax = totalImpuesto.toString()
        invoiceDetallePreventa1.p_total = totalTotal.toString()

        invoiceDetallePreventa1.p_changing = "0"
        invoiceDetallePreventa1.p_note = notes!!.text.toString()
        invoiceDetallePreventa1.p_canceled = "1"
        invoiceDetallePreventa1.p_paid = "0"
        invoiceDetallePreventa1.p_user_id = idUsuario
        invoiceDetallePreventa1.p_user_id_applied = idUsuario
        invoiceDetallePreventa1.p_sale = activity!!.currentVenta

        if (tipoFacturacion == "Preventa") {
            invoiceDetallePreventa1.facturaDePreventa = "Preventa"
        } else if (tipoFacturacion == "Proforma") {
            invoiceDetallePreventa1.facturaDePreventa = "Proforma"
        }


        Log.d("actFactDetPrev", invoiceDetallePreventa1.toString() + "")
    }


    protected fun actualizarNumeracion() {
        val id = facturaId!!.toInt()

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        if (tipoFacturacion == "Preventa") {
            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                num_actualizada =
                    realm3.where(Numeracion::class.java).equalTo("number", id)
                        .equalTo("sale_type", "2").findFirst()
                num_actualizada!!.rec_aplicada = 1
                realm3.insertOrUpdate(num_actualizada)
                realm3.close()
                Log.d("Numeracion", num_actualizada.toString() + "")
            }
        } else if (tipoFacturacion == "Proforma") {
            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                num_actualizada =
                    realm3.where(Numeracion::class.java).equalTo("number", id)
                        .equalTo("sale_type", "3").findFirst()
                num_actualizada!!.rec_aplicada = 1
                realm3.insertOrUpdate(num_actualizada)
                realm3.close()
                Log.d("Numeracion", num_actualizada.toString() + "")
            }
        }
    }

    protected fun actualizarFactura() {
        val invoice = activity!!.invoiceByInvoiceDetalles

        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync(
            { realm ->
                realm.copyToRealmOrUpdate(
                    invoice
                )
            }, {
                actualizarVenta()
                actualizarDatosTotales()
            },
            { error ->
                Log.e(
                    "actualizarFactura ",
                    error.message!!
                )
            })

        Log.d("invoicetotal", invoice.toString() + "")
    }

    protected fun actualizarVenta() {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()
        realm3.executeTransaction { realm3 ->
            Log.d("ENVIADOSALE", facturaId!!)
            sale_actualizada =
                realm3.where(sale::class.java).equalTo("invoice_id", facturaId).findFirst()
            Log.d(
                "dadasdad",
                client_name!!.text.toString()
            )
            Log.d("ENVIADOSALE", sale_actualizada.toString() + "")
            val nombreEscrito = client_name!!.text.toString()

            if (nombreEscrito.matches(" ".toRegex())) {
                Log.d("NombrenoCambio", "Nombre no cambio")
            } else {
                Log.d("NombreCambio", "Nombre cambio")
                sale_actualizada!!.customer_name =
                    client_name!!.text.toString()
            }

            sale_actualizada!!.applied = "1"
            sale_actualizada!!.updated_at =
                date + " " + get24Time()


            realm3.insertOrUpdate(sale_actualizada)
            realm3.close()
            Log.d("ENVIADOSALE", sale_actualizada.toString() + "")
        }
    }


    protected fun aplicarFactura() {
        actualizarFacturaDetalles()

        Toast.makeText(getActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show()

        applyBill!!.visibility = View.INVISIBLE
        //  printBill.setVisibility(View.VISIBLE);
        apply_done = 1
    }

    protected fun actualizarDatosTotales() {
        if (tipoFacturacion == "Preventa") {
            val realm5 = Realm.getDefaultInstance()

            realm5.beginTransaction()
            val currentIdNum = realm5.where(datosTotales::class.java).max("id")

            nextId = if (currentIdNum == null) {
                1
            } else {
                currentIdNum.toInt() + 1
            }


            val datos_actualizados = datosTotales()

            datos_actualizados.id = nextId
            datos_actualizados.idTotal = 3
            datos_actualizados.nombreTotal = "Preventa"
            datos_actualizados.totalPreventa = totalTotal
            datos_actualizados.date = date
            // datos_actualizados.setDate("2018-12-04");
            realm5.copyToRealmOrUpdate(datos_actualizados)
            realm5.commitTransaction()
            Log.d("datosTotalesPrev", datos_actualizados.toString() + "")
            realm5.close()
        } else if (tipoFacturacion == "Proforma") {
            val realm5 = Realm.getDefaultInstance()

            realm5.beginTransaction()
            val currentIdNum = realm5.where(datosTotales::class.java).max("id")

            nextId = if (currentIdNum == null) {
                1
            } else {
                currentIdNum.toInt() + 1
            }


            val datos_actualizados = datosTotales()

            datos_actualizados.id = nextId
            datos_actualizados.idTotal = 4
            datos_actualizados.nombreTotal = "Proforma"
            datos_actualizados.totalProforma = totalTotal
            datos_actualizados.date = date
            //  datos_actualizados.setDate("2018-12-04");
            realm5.copyToRealmOrUpdate(datos_actualizados)
            realm5.commitTransaction()
            Log.d("datosTotalesPrf", datos_actualizados.toString() + "")
            realm5.close()
        }
    }

    companion object {
        var handler: Handler? = null
        var runnable: Runnable? = null
        private var subGra: TextView? = null
        private var subExe: TextView? = null
        private var subT: TextView? = null
        private var ivaSub: TextView? = null
        private var discount: TextView? = null
        private var Total: TextView? = null

        private var notes: EditText? = null
        private var client_name: EditText? = null

        private var applyBill: Button? = null
        private val printBill: Button? = null

        private var apply_done = 0

        /*

    protected void actualizarDatosTotales() {
        if(tipoFacturacion.equals("Preventa")) {
            final Realm realm3 = Realm.getDefaultInstance();
            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {
                    datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "Preventa").findFirst();

                    totalDatosTotal = datos_actualizados.getTotalPreventa();

                    totalDatosTotal2 = totalDatosTotal + totalTotal;

                    datos_actualizados.setTotalPreventa(totalDatosTotal2);
                    datos_actualizados.setDate("2018-12-03");

                    realm3.insertOrUpdate(datos_actualizados);
                    realm3.close();

                    Log.d("TotalDatos", datos_actualizados + "");
                }
            });
        }
        else if(tipoFacturacion.equals("Proforma")){
            final Realm realm3 = Realm.getDefaultInstance();
            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {
                    datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "Proforma").findFirst();

                    totalDatosTotal = datos_actualizados.getTotalProforma();

                    totalDatosTotal2 = totalDatosTotal + totalTotal;

                    datos_actualizados.setTotalProforma(totalDatosTotal2);
                    datos_actualizados.setDate(Functions.getDate());

                    realm3.insertOrUpdate(datos_actualizados);
                    realm3.close();

                    Log.d("TotalDatos", datos_actualizados + "");
                }
            });
        }
    }*/
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