package com.friendlysystemgroup.friendlypos.ventadirecta.fragment

import android.app.Activity
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
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.application.util.Functions.date
import com.friendlysystemgroup.friendlypos.application.util.Functions.dateConsecutivo
import com.friendlysystemgroup.friendlypos.application.util.Functions.get24Time
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions.imprimirFacturaVentaDirectaTotal
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.distribucion.util.GPSTracker
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.preventas.modelo.Numeracion
import com.friendlysystemgroup.friendlypos.principal.modelo.ConsecutivosNumberFe
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import com.friendlysystemgroup.friendlypos.principal.modelo.datosTotales
import com.friendlysystemgroup.friendlypos.ventadirecta.activity.VentaDirectaActivity
import io.realm.Realm
import java.util.Random

class VentaDirTotalizarFragment : BaseFragment() {
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
    var consConsecutivo: String? = null
    var consConsecutivoATV: String? = null
    var numeroConsecutivo: String? = null
    var keyElectronica: String? = null
    var slecTAB: Int = 0
    var sale_actualizada: sale? = null

    var num_actualizada: Numeracion? = null
    var activity: VentaDirectaActivity? = null
    var nextId: Int = 0
    var gps: GPSTracker? = null
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null

    var seleccion: String? = null


    var datos_actualizados: datosTotales? = null
    var totalDatosTotal: Double = 0.0
    var totalDatosTotal2: Double = 0.0

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as VentaDirectaActivity
    }

    override fun onPause() {
        super.onPause()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver!!.setBluetoothStateChangeReceiver(context!!)
        session = SessionPrefes(requireContext())
        Log.d("applydone", apply_done.toString() + "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ventadir_totalizar, container, false)


        client_name = rootView.findViewById<View>(R.id.client_name) as EditText

        subExe = rootView.findViewById<View>(R.id.subExento) as TextView
        subGra = rootView.findViewById<View>(R.id.subGrabado) as TextView
        ivaSub = rootView.findViewById<View>(R.id.IvaFact) as TextView
        subT = rootView.findViewById<View>(R.id.subTotal) as TextView
        discount = rootView.findViewById<View>(R.id.Discount) as TextView
        Total = rootView.findViewById<View>(R.id.Total) as TextView

        paid = rootView.findViewById<View>(R.id.txtPaid) as EditText

        change = rootView.findViewById<View>(R.id.txtChange) as TextView
        slecTAB = (getActivity() as VentaDirectaActivity).selecClienteTabVentaDirecta

        if (slecTAB == 1) {
            metodoPagoCliente =
                (getActivity() as VentaDirectaActivity).metodoPagoClienteVentaDirecta


            if (metodoPagoCliente == "1") {
                //bill_type = 1;
                try {
                    paid!!.isEnabled = true
                } catch (e: Exception) {
                    Log.d("JD", "Error " + e.message)
                }
            } else if (metodoPagoCliente == "2") {
                //  bill_type = 2;
                paid!!.isEnabled = false
            }
        } else {
            Log.d("nadaTotalizar", "nadaTotalizar")
        }
        notes = rootView.findViewById<View>(R.id.txtNotes) as EditText

        applyBill = rootView.findViewById<View>(R.id.applyInvoice) as Button
        printBill = rootView.findViewById<View>(R.id.printInvoice) as Button

        if (apply_done == 1) {
            applyBill!!.visibility = View.GONE
            printBill!!.visibility = View.VISIBLE
        } else {
            applyBill!!.visibility = View.VISIBLE
            printBill!!.visibility = View.GONE
        }

        applyBill!!.setOnClickListener {
            try {
                if (metodoPagoCliente == "1") {
                    if (paid!!.text.toString().isEmpty()) {
                        pagoCon = 0.0
                        vuelto = 0.0
                        totalVuelvo = String.format(
                            "%,.2f",
                            vuelto
                        )
                        change!!.text = totalVuelvo
                    } else {
                        pagoCon =
                            paid!!.text.toString()
                                .toDouble()
                        totalPagoCon = String.format(
                            "%,.2f",
                            pagoCon
                        )
                        val total = totalTotal

                        if (pagoCon >= total) {
                            vuelto =
                                pagoCon - total
                            totalVuelvo = String.format(
                                "%,.2f",
                                vuelto
                            )
                            change!!.text = totalVuelvo
                        } else {
                            Toast.makeText(
                                getActivity(),
                                "Digite una cantidad mayor al total",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    val tabCliente = 0
                    (getActivity() as VentaDirectaActivity).selecClienteTabVentaDirecta =
                        tabCliente
                    obtenerLocalización()
                    aplicarFactura()
                } else if (metodoPagoCliente == "2") {
                    Toast.makeText(getActivity(), "Crédito", Toast.LENGTH_LONG).show()
                    obtenerLocalización()
                    aplicarFactura()
                    val tabCliente = 0
                    (getActivity() as VentaDirectaActivity).selecClienteTabVentaDirecta =
                        tabCliente
                }
                actualizarFactura()
                actualizarNumeracion()
            } catch (e: Exception) {
                Toast.makeText(getActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
            session!!.guardarDatosBloquearBotonesDevolver(0)
        }

        printBill!!.setOnClickListener {
            try {
                if (bluetoothStateChangeReceiver!!.isBluetoothAvailable == true) {
                    val layoutInflater = LayoutInflater.from(getActivity())
                    val promptView =
                        layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)

                    val alertDialogBuilder = AlertDialog.Builder(
                        getActivity()!!
                    )
                    alertDialogBuilder.setView(promptView)
                    val checkbox =
                        promptView.findViewById<View>(R.id.checkbox) as CheckBox

                    val label =
                        promptView.findViewById<View>(R.id.promtClabelRecibosImp) as TextView
                    label.text = "Escriba el número de impresiones requeridas"

                    val input =
                        promptView.findViewById<View>(R.id.promtCtextRecibosImp) as EditText

                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton(
                        "OK"
                    ) { dialog, id ->
                        val cantidadImpresiones = input.text.toString()
                        imprimirFacturaVentaDirectaTotal(
                            sale_actualizada,
                            getActivity()!!, 3, cantidadImpresiones
                        )
                        clearAll()
                        Log.d(
                            "applydoneImp",
                            apply_done.toString() + ""
                        )
                    }
                    alertDialogBuilder.setNegativeButton(
                        "Cancel"
                    ) { dialog, id -> dialog.cancel() }

                    val alertD = alertDialogBuilder.create()
                    alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    alertD.show()
                } else if (bluetoothStateChangeReceiver!!.isBluetoothAvailable == false) {
                    CreateMessage(
                        getActivity()!!,
                        "Error",
                        "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                    )
                }
            } catch (e: Exception) {
                CreateMessage(
                    getActivity()!!,
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
            paid!!.text.clear()

            totalGrabado = (getActivity() as VentaDirectaActivity).getTotalizarSubGrabado()
            totalExento = (getActivity() as VentaDirectaActivity).getTotalizarSubExento()
            totalSubtotal = (getActivity() as VentaDirectaActivity).getTotalizarSubTotal()
            totalDescuento = (getActivity() as VentaDirectaActivity).getTotalizarDescuento()
            totalImpuesto = (getActivity() as VentaDirectaActivity).getTotalizarImpuestoIVA()
            totalTotal = (getActivity() as VentaDirectaActivity).getTotalizarTotal()
            facturaId = (getActivity() as VentaDirectaActivity).invoiceIdVentaDirecta.toString()

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
            Total!!.text =
                String.format("%,.2f", totalTotal)

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


    protected fun actualizarNumeracion() {
        val id = facturaId!!.toInt()
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        val realm3 = Realm.getDefaultInstance()
        realm3.executeTransaction { realm3 ->
            num_actualizada = realm3.where(Numeracion::class.java).equalTo("number", id)
                .equalTo("sale_type", "1").findFirst()
            num_actualizada!!.rec_aplicada = 1
            realm3.insertOrUpdate(num_actualizada)
            realm3.close()
            Log.d("Numeracion", num_actualizada.toString() + "")
        }
    }


    fun actualizarFacturaDetalles() {
        val realm = Realm.getDefaultInstance()
        usuer = session!!.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        val idUsuario = usuarios!!.id
        realm.close()

        val invoiceDetallePreventa1 = activity!!.currentInvoice

        invoiceDetallePreventa1.p_key = keyElectronica
        invoiceDetallePreventa1.p_consecutive_number = numeroConsecutivo

        invoiceDetallePreventa1.p_longitud = longitude
        invoiceDetallePreventa1.p_latitud = latitude

        invoiceDetallePreventa1.p_subtotal = totalSubtotal.toString()
        invoiceDetallePreventa1.p_subtotal_taxed = totalGrabado.toString()
        invoiceDetallePreventa1.p_subtotal_exempt = totalExento.toString()
        invoiceDetallePreventa1.p_discount = totalDescuento.toString()
        invoiceDetallePreventa1.p_tax = totalImpuesto.toString()
        invoiceDetallePreventa1.p_total = totalTotal.toString()

        invoiceDetallePreventa1.p_changing = vuelto.toString()
        val nota = if (notes!!.text.toString().isEmpty()) {
            "ninguna"
        } else {
            notes!!.text.toString()
        }

        invoiceDetallePreventa1.p_note = nota
        invoiceDetallePreventa1.p_canceled = "1"
        invoiceDetallePreventa1.p_paid = pagoCon.toString()
        invoiceDetallePreventa1.p_user_id = idUsuario
        invoiceDetallePreventa1.p_user_id_applied = idUsuario
        invoiceDetallePreventa1.p_sale = activity!!.currentVenta
        invoiceDetallePreventa1.facturaDePreventa = "VentaDirecta"
        invoiceDetallePreventa1.p_Aplicada = 1

        Log.d("actFactDetVD", invoiceDetallePreventa1.toString() + "")
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
            sale_actualizada =
                realm3.where(sale::class.java).equalTo("invoice_id", facturaId).findFirst()
            Log.d(
                "dadasdad",
                client_name!!.text.toString()
            )

            val nombreEscrito = client_name!!.text.toString()

            if (nombreEscrito.matches(" ".toRegex())) {
                Log.d("nombreCam", "Nombre no cambio")
            } else {
                Log.d("nombreCam", "Nombre cambio")
                sale_actualizada!!.customer_name =
                    client_name!!.text.toString()
            }

            sale_actualizada!!.applied = "1"
            sale_actualizada!!.updated_at =
                date + " " + get24Time()
            sale_actualizada!!.aplicada = 1
            sale_actualizada!!.subida = 1

            realm3.insertOrUpdate(sale_actualizada)
            realm3.close()
            Log.d("ENVIADOSALE", sale_actualizada.toString() + "")
        }
    }

    protected fun actualizarDatosTotales() {
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
        datos_actualizados.idTotal = 2
        datos_actualizados.nombreTotal = "VentaDirecta"
        datos_actualizados.totalVentaDirecta = totalTotal
        datos_actualizados.date = date
        //    datos_actualizados.setDate("2018-12-04");
        realm5.copyToRealmOrUpdate(datos_actualizados)
        realm5.commitTransaction()
        Log.d("datosTotalesVD", datos_actualizados.toString() + "")
        realm5.close()
    }


    /*

    protected void actualizarDatosTotales() {

        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "VentaDirecta").findFirst();

                totalDatosTotal = datos_actualizados.getTotalVentaDirecta();

                totalDatosTotal2 = totalDatosTotal + totalTotal;

                datos_actualizados.setTotalVentaDirecta(totalDatosTotal2);
                datos_actualizados.setDate(Functions.getDate());

                realm3.insertOrUpdate(datos_actualizados);
                realm3.close();

                Log.d("TotalDatos", datos_actualizados + "" );
            }
        });

    }*/
    protected fun aplicarFactura() {
        paid!!.isEnabled = false
        crearFacturaElectronica()
        actualizarFacturaDetalles()

        Toast.makeText(getActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show()

        applyBill!!.visibility = View.GONE
        printBill!!.visibility = View.VISIBLE
        apply_done = 1
    }


    fun crearFacturaElectronica() {
        val realm = Realm.getDefaultInstance()

        val sysconf = realm.where(Sysconf::class.java).findFirst()

        val sysSucursal = sysconf!!.sucursal

        usuer = session!!.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        val userTerminal = usuarios!!.terminal
        val userId = usuarios.id

        val invoiceDetallePreventa1 = activity!!.currentInvoice


        //   invoice factura_actualizada = realm.where(invoice.class).equalTo("id", facturaId).findFirst();
        val invType = invoiceDetallePreventa1.p_type

        // ConsecutivosNumberFe consecutivosNumberFe = realm.where(ConsecutivosNumberFe.class).equalTo("user_id", "1").findFirst();
        //   String consConsecutivo = consecutivosNumberFe.getNumber_consecutive();
        Log.d("sysSucursal", sysSucursal!!)
        Log.d("userTerminal", userTerminal!!)
        Log.d("invType", invType)

        val realm2 = Realm.getDefaultInstance()

        realm2.executeTransaction { realm ->
            val numero = realm.where(ConsecutivosNumberFe::class.java)
                .equalTo("user_id", userId).equalTo("type_doc", invType).max("number_consecutive")
            nextId = if (numero == null) {
                1
            } else {
                numero.toInt() + 1
            }
            val valor = numero!!.toInt()

            val length = nextId.toString().length
            if (length == 1) {
                consConsecutivo = "000000000$nextId"
            } else if (length == 2) {
                consConsecutivo = "00000000$nextId"
            } else if (length == 3) {
                consConsecutivo = "0000000$nextId"
            } else if (length == 4) {
                consConsecutivo = "000000$nextId"
            } else if (length == 5) {
                consConsecutivo = "00000$nextId"
            } else if (length == 6) {
                consConsecutivo = "0000$nextId"
            } else if (length == 7) {
                consConsecutivo = "000$nextId"
            } else if (length == 8) {
                consConsecutivo = "00$nextId"
            } else if (length == 9) {
                consConsecutivo = "0$nextId"
            } else if (length == 10) {
                consConsecutivo = "" + nextId
            }
        }




        Log.d("consConsecutivo", consConsecutivo!!)

        numeroConsecutivo = sysSucursal + userTerminal + invType + consConsecutivo

        Log.d("numeroConsecutivo", numeroConsecutivo!!)


        val realm5 = Realm.getDefaultInstance()
        realm5.executeTransaction { realm5 ->
            val numNuevo = realm5.where(
                ConsecutivosNumberFe::class.java
            ).equalTo("user_id", userId).equalTo("type_doc", invType).findFirst()
            numNuevo!!.number_consecutive = nextId

            realm5.insertOrUpdate(numNuevo)
            Log.d("ActConsecutivo", numNuevo.toString() + "")
            realm5.close()
        }
        realm5.close()


        val sysIdNumberAtv = sysconf.id_number_atv

        val lengthAtv = sysIdNumberAtv.toString().length

        if (lengthAtv == 1) {
            consConsecutivoATV = "00000000000$sysIdNumberAtv"
        } else if (lengthAtv == 2) {
            consConsecutivoATV = "0000000000$sysIdNumberAtv"
        } else if (lengthAtv == 3) {
            consConsecutivoATV = "000000000$sysIdNumberAtv"
        } else if (lengthAtv == 4) {
            consConsecutivoATV = "00000000$sysIdNumberAtv"
        } else if (lengthAtv == 5) {
            consConsecutivoATV = "0000000$sysIdNumberAtv"
        } else if (lengthAtv == 6) {
            consConsecutivoATV = "000000$sysIdNumberAtv"
        } else if (lengthAtv == 7) {
            consConsecutivoATV = "00000$sysIdNumberAtv"
        } else if (lengthAtv == 8) {
            consConsecutivoATV = "0000$sysIdNumberAtv"
        } else if (lengthAtv == 9) {
            consConsecutivoATV = "000$sysIdNumberAtv"
        } else if (lengthAtv == 10) {
            consConsecutivoATV = "00$sysIdNumberAtv"
        } else if (lengthAtv == 11) {
            consConsecutivoATV = "0$sysIdNumberAtv"
        } else if (lengthAtv == 12) {
            consConsecutivoATV = "" + sysIdNumberAtv
        }

        val min = 10000001
        val max = 99999999
        val codSeguridad = Random().nextInt((max - min) + 1) + min


        Log.d("consConsecutivoATV", consConsecutivoATV!!)
        Log.d("userTerminal", userTerminal)
        Log.d("invType", invType)
        Log.d("codSeguridad", codSeguridad.toString() + "")

        keyElectronica =
            "506" + dateConsecutivo + consConsecutivoATV + numeroConsecutivo + "3" + codSeguridad
        Log.d("keyElectronica", keyElectronica + "")



        realm.close()
    }


    companion object {
        private var subGra: TextView? = null
        private var subExe: TextView? = null
        private var subT: TextView? = null
        private var ivaSub: TextView? = null
        private var discount: TextView? = null
        private var Total: TextView? = null
        private var change: TextView? = null


        private var notes: EditText? = null
        private var client_name: EditText? = null
        private var paid: EditText? = null

        var pagoCon: Double = 0.0
        var vuelto: Double = 0.0
        private var applyBill: Button? = null
        private var printBill: Button? = null

        private var apply_done = 0
        fun clearAll() {
            if (apply_done == 1) {
                apply_done = 0
                paid!!.text.clear()
                Log.d("applydoneClear", apply_done.toString() + "")
            }
            try {
                System.gc()
            } catch (e: Exception) {
            }
        }
    }
}