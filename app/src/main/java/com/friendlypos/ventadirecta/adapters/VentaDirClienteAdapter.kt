package com.friendlysystemgroup.friendlypos.ventadirecta.adapters

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.application.util.Functions.date
import com.friendlysystemgroup.friendlypos.application.util.Functions.get24Time
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.util.GPSTracker
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.preventas.modelo.Numeracion
import com.friendlysystemgroup.friendlypos.preventas.modelo.visit
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.ventadirecta.activity.VentaDirectaActivity
import com.friendlysystemgroup.friendlypos.ventadirecta.fragment.VentaDirResumenFragment
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

/**
 * Created by DelvoM on 13/08/2018.
 */

class VentaDirClienteAdapter(
    context: Context,
    private val activity: VentaDirectaActivity,
    var contentList: MutableList<Clientes>
) :
    RecyclerView.Adapter<VentaDirClienteAdapter.CharacterViewHolder>() {
    private var selected_position = -1
    var tabCliente: Int = 0
    var nextId: Int = 0
    var metodoPagoId: String? = null
    var idCliente: String? = null
    var nombreCliente: String? = null
    var fecha: String? = null
    var usuer: String? = null
    var feCliente: String? = null
    var idTypeInvoice: String? = null
    var session: SessionPrefes
    var rbcomprado: RadioButton? = null
    var rbvisitado: RadioButton? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var seleccion: String? = null
    var gps: GPSTracker? = null
    var observ: String? = null
    var idUsuario: String? = null
    var numFactura: String? = null

    private val fragment: VentaDirResumenFragment? = null
    var facturaid1: List<Pivot>? = null
    var idFacturaSeleccionada: String? = null
    var idInvetarioSelec: Int = 0
    var amount_inventario: Double = 0.0

    init {
        QuickContext = context
        session = SessionPrefes(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_ventadirecta_clientes, parent, false)

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val content = contentList[position]

        creditolimite = content.creditLimit!!.toDouble()
        descuentoFixed = content.fixedDiscount!!.toDouble()
        cleintedue = content.due!!.toDouble()
        credittime = content.creditTime!!.toDouble()
        val cardCliente = content.card
        val companyCliente = content.companyName
        val fantasyCliente = content.fantasyName

        holder.txt_prev_card.text = cardCliente
        holder.txt_prev_fantasyname.text = fantasyCliente
        holder.txt_prev_companyname.text = companyCliente
        holder.txt_prev_creditlimit.text = String.format("%,.2f", (creditolimite))
        holder.txt_prev_fixeddescount.text = String.format("%,.2f", (descuentoFixed))
        holder.txt_prev_due.text = String.format("%,.2f", (cleintedue))
        holder.txt_prev_credittime.text = String.format("%,.2f", (credittime))

        holder.cardView.setOnClickListener(View.OnClickListener {
            val pos = position
            if (pos == RecyclerView.NO_POSITION) return@OnClickListener

            notifyItemChanged(selected_position)
            selected_position = position
            notifyItemChanged(selected_position)

            val tabClienteInicio = activity.selecClienteTabVentaDirecta
            if (tabClienteInicio == 0) {
                val clickedDataItem = contentList[pos]

                idCliente = clickedDataItem.id
                feCliente = clickedDataItem.fe

                nombreCliente = clickedDataItem.name
                val creditoTime = clickedDataItem.creditTime!!.toInt()
                val creditoLimiteClienteP = clickedDataItem.creditLimit
                val dueClienteP = clickedDataItem.due

                val layoutInflater = LayoutInflater.from(QuickContext)
                val promptView = layoutInflater.inflate(R.layout.promptclient_preventa, null)

                val dialogInicial = Dialog(
                    QuickContext!!
                )
                dialogInicial.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogInicial.setContentView(R.layout.promptvisitado_ventadirecta)

                dialogInicial.window!!
                    .setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                rbcomprado =
                    dialogInicial.findViewById<View>(R.id.compradoBillVisitado) as RadioButton
                rbvisitado =
                    dialogInicial.findViewById<View>(R.id.visitadoBillVisitado) as RadioButton
                val btnOkVisitado = dialogInicial.findViewById<View>(R.id.btnOKV) as Button
                val btnOkComprado = dialogInicial.findViewById<View>(R.id.btnOKC) as Button
                val btnCancel = dialogInicial.findViewById<View>(R.id.btnCancel) as Button
                txtObservaciones =
                    dialogInicial.findViewById<View>(R.id.txtObservaciones) as EditText
                dialogInicial.show()

                val yourRadioGroup =
                    dialogInicial.findViewById<View>(R.id.rgTipoVisitado) as RadioGroup
                yourRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.visitadoBillVisitado -> {
                            btnOkVisitado.visibility = View.VISIBLE
                            btnOkComprado.visibility = View.INVISIBLE
                        }

                        R.id.compradoBillVisitado -> {
                            btnOkVisitado.visibility = View.INVISIBLE
                            btnOkComprado.visibility = View.VISIBLE
                        }
                    }
                }

                btnOkVisitado.setOnClickListener {
                    obtenerLocalización()
                    if (!txtObservaciones!!.text.toString()
                            .isEmpty()
                    ) {
                        observ =
                            txtObservaciones!!.text.toString()
                        seleccion = "2"

                        actualizarClienteVisitado()
                        dialogInicial.dismiss()
                        holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else {
                        txtObservaciones!!.error =
                            "Campo requerido"
                        txtObservaciones!!.requestFocus()
                    }
                }

                btnOkComprado.setOnClickListener {
                    dialogInicial.dismiss()
                    obtenerLocalización()
                    fecha =
                        date + " " + get24Time()
                    seleccion = "1"
                    val layoutInflater =
                        LayoutInflater.from(QuickContext)
                    val promptView =
                        layoutInflater.inflate(R.layout.promptclient_preventa, null)

                    val alertDialogBuilder = AlertDialog.Builder(
                        QuickContext!!
                    )
                    alertDialogBuilder.setView(promptView)
                    val rbcontado =
                        promptView.findViewById<View>(R.id.contadoBill) as RadioButton
                    val rbcredito =
                        promptView.findViewById<View>(R.id.creditBill) as RadioButton

                    if (creditoTime == 0) {
                        rbcredito.visibility = View.GONE
                    }

                    alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton(
                            "OK"
                        ) { dialog, id ->
                            fecha =
                                date + " " + get24Time()
                            if (!rbcontado.isChecked && !rbcredito.isChecked) {
                                CreateMessage(
                                    QuickContext!!,
                                    " ",
                                    "Debe seleccionar una opción"
                                )
                            } else {
                                if (rbcredito.isChecked) {
                                    txtObservaciones!!.setText(
                                        " "
                                    )
                                    observ =
                                        txtObservaciones!!.text.toString()
                                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                                    metodoPagoId = "2"
                                    notifyDataSetChanged()
                                    agregar()
                                    tabCliente = 1
                                    activity.selecClienteTabVentaDirecta = tabCliente
                                    activity.creditoLimiteClienteVentaDirecta =
                                        creditoLimiteClienteP
                                    activity.dueClienteVentaDirecta = dueClienteP

                                    //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                    activity.setInvoiceIdPreventa(nextId)
                                    activity.metodoPagoClienteVentaDirecta = metodoPagoId


                                    /*final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando",
                                                                                                    "Seleccionando Cliente", true);*/
                                    val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                                                                        "Seleccionando Cliente", true);*/
                                    val message = "Seleccionando Cliente"
                                    val titulo = "Cargando"
                                    val spannableString = SpannableString(message)
                                    val spannableStringTitulo = SpannableString(titulo)

                                    val typefaceSpan = CalligraphyTypefaceSpan(
                                        TypefaceUtils.load(
                                            QuickContext!!.assets,
                                            "font/monse.otf"
                                        )
                                    )
                                    spannableString.setSpan(
                                        typefaceSpan,
                                        0,
                                        message.length,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    spannableStringTitulo.setSpan(
                                        typefaceSpan,
                                        0,
                                        titulo.length,
                                        Spanned.SPAN_PRIORITY
                                    )

                                    progresRing.setTitle(spannableStringTitulo)
                                    progresRing.setMessage(spannableString)
                                    progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                                    progresRing.isIndeterminate = true
                                    progresRing.setCancelable(true)
                                    progresRing.show()
                                    progresRing.setCancelable(true)
                                    Thread {
                                        try {
                                            Thread.sleep(5000)
                                        } catch (e: Exception) {
                                        }
                                        progresRing.dismiss()
                                    }.start()
                                    actualizarClienteVisitado()
                                } else if (rbcontado.isChecked) {
                                    txtObservaciones!!.setText(
                                        " "
                                    )
                                    observ =
                                        txtObservaciones!!.text.toString()
                                    metodoPagoId = "1"
                                    notifyDataSetChanged()
                                    agregar()
                                    tabCliente = 1
                                    activity.selecClienteTabVentaDirecta = tabCliente
                                    activity.creditoLimiteClienteVentaDirecta =
                                        creditoLimiteClienteP
                                    activity.dueClienteVentaDirecta = dueClienteP

                                    //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                    activity.setInvoiceIdPreventa(nextId)
                                    activity.metodoPagoClienteVentaDirecta = metodoPagoId


                                    /* final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                                                                                            progresRing.setCancelable(true);*/
                                    val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                                                                        "Seleccionando Cliente", true);*/
                                    val message = "Seleccionando Cliente"
                                    val titulo = "Cargando"
                                    val spannableString = SpannableString(message)
                                    val spannableStringTitulo = SpannableString(titulo)

                                    val typefaceSpan = CalligraphyTypefaceSpan(
                                        TypefaceUtils.load(
                                            QuickContext!!.assets,
                                            "font/monse.otf"
                                        )
                                    )
                                    spannableString.setSpan(
                                        typefaceSpan,
                                        0,
                                        message.length,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    spannableStringTitulo.setSpan(
                                        typefaceSpan,
                                        0,
                                        titulo.length,
                                        Spanned.SPAN_PRIORITY
                                    )

                                    progresRing.setTitle(spannableStringTitulo)
                                    progresRing.setMessage(spannableString)
                                    progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                                    progresRing.isIndeterminate = true
                                    progresRing.setCancelable(true)
                                    progresRing.show()
                                    Thread {
                                        try {
                                            Thread.sleep(5000)
                                        } catch (e: Exception) {
                                        }
                                        progresRing.dismiss()
                                    }.start()
                                    actualizarClienteVisitado()
                                }
                                val nextId2: Int
                                val realm5 = Realm.getDefaultInstance()

                                realm5.beginTransaction()
                                val currentIdNum =
                                    realm5.where(Pivot::class.java).max("id")


                                nextId2 = currentIdNum?.toInt() ?: 0

                                session.guardarDatosPivotVentaDirecta(nextId2)
                                Log.d("currentIdNum", "" + nextId2)
                                realm5.commitTransaction()
                                realm5.close()
                            }
                        }
                        .setNegativeButton(
                            "Cancel"
                        ) { dialog, id -> dialog.cancel() }


                    val alertSeg = alertDialogBuilder.create()
                    alertSeg.show()
                }

                btnCancel.setOnClickListener { dialogInicial.dismiss() }
            }
            if (tabClienteInicio == 1) {
                val message = "¿Desea cancelar la factura en proceso?"
                val titulo = "Salir"
                val spannableString = SpannableString(message)
                val spannableStringTitulo = SpannableString(titulo)

                val typefaceSpan = CalligraphyTypefaceSpan(
                    TypefaceUtils.load(
                        QuickContext!!.assets, "font/monse.otf"
                    )
                )
                spannableString.setSpan(
                    typefaceSpan,
                    0,
                    message.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length, Spanned.SPAN_PRIORITY)

                val dialogReturnSale = AlertDialog.Builder(
                    QuickContext!!
                )

                    .setTitle(spannableStringTitulo)
                    .setMessage(spannableString)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        val realm2 = Realm.getDefaultInstance()
                        realm2.executeTransaction { realm -> // increment index
                            /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();
                                            
                                                                                if(numeracion.getId()){}
                                            */

                            // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                            val numero = realm.where(Numeracion::class.java)
                                .equalTo("sale_type", "1").max("number")
                            nextId = if (numero == null) {
                                1
                            } else {
                                numero.toInt() - 1
                            }
                        }


                        val realm5 = Realm.getDefaultInstance()
                        realm5.executeTransaction { realm5 ->
                            val numNuevo = Numeracion() // unmanaged
                            numNuevo.sale_type = "1"
                            numNuevo.numeracion_numero = nextId

                            realm5.insertOrUpdate(numNuevo)
                            Log.d("VDNumNuevaAtras", numNuevo.toString() + "")
                        }
                        realm5.close()
                        devolverTodo()
                        notifyDataSetChanged()


                        val clickedDataItem = contentList[pos]

                        idCliente = clickedDataItem.id
                        feCliente = clickedDataItem.fe

                        nombreCliente = clickedDataItem.name
                        val creditoTime = clickedDataItem.creditTime!!.toInt()
                        val creditoLimiteClienteP = clickedDataItem.creditLimit
                        val dueClienteP = clickedDataItem.due

                        val layoutInflater =
                            LayoutInflater.from(QuickContext)
                        val promptView =
                            layoutInflater.inflate(R.layout.promptclient_preventa, null)

                        val dialogInicial = Dialog(
                            QuickContext!!
                        )
                        dialogInicial.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialogInicial.setContentView(R.layout.promptvisitado_ventadirecta)

                        dialogInicial.window!!
                            .setLayout(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.WRAP_CONTENT
                            )
                        rbcomprado =
                            dialogInicial.findViewById<View>(R.id.compradoBillVisitado) as RadioButton
                        rbvisitado =
                            dialogInicial.findViewById<View>(R.id.visitadoBillVisitado) as RadioButton
                        val btnOkVisitado =
                            dialogInicial.findViewById<View>(R.id.btnOKV) as Button
                        val btnOkComprado =
                            dialogInicial.findViewById<View>(R.id.btnOKC) as Button
                        val btnCancel =
                            dialogInicial.findViewById<View>(R.id.btnCancel) as Button
                        txtObservaciones =
                            dialogInicial.findViewById<View>(R.id.txtObservaciones) as EditText
                        dialogInicial.show()

                        val yourRadioGroup =
                            dialogInicial.findViewById<View>(R.id.rgTipoVisitado) as RadioGroup
                        yourRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                            when (checkedId) {
                                R.id.visitadoBillVisitado -> {
                                    btnOkVisitado.visibility = View.VISIBLE
                                    btnOkComprado.visibility = View.INVISIBLE
                                }

                                R.id.compradoBillVisitado -> {
                                    btnOkVisitado.visibility = View.INVISIBLE
                                    btnOkComprado.visibility = View.VISIBLE
                                }
                            }
                        }

                        btnOkVisitado.setOnClickListener {
                            obtenerLocalización()
                            if (!txtObservaciones!!.text.toString()
                                    .isEmpty()
                            ) {
                                observ =
                                    txtObservaciones!!.text.toString()
                                seleccion = "2"

                                actualizarClienteVisitado()
                                dialogInicial.dismiss()
                                holder.cardView.setBackgroundColor(
                                    Color.parseColor(
                                        "#FFFFFF"
                                    )
                                )
                            } else {
                                txtObservaciones!!.error =
                                    "Campo requerido"
                                txtObservaciones!!.requestFocus()
                            }
                        }

                        btnOkComprado.setOnClickListener {
                            dialogInicial.dismiss()
                            obtenerLocalización()
                            fecha =
                                date + " " + get24Time()
                            seleccion = "1"
                            val layoutInflater =
                                LayoutInflater.from(QuickContext)
                            val promptView =
                                layoutInflater.inflate(R.layout.promptclient_preventa, null)

                            val alertDialogBuilder = AlertDialog.Builder(
                                QuickContext!!
                            )
                            alertDialogBuilder.setView(promptView)
                            val rbcontado =
                                promptView.findViewById<View>(R.id.contadoBill) as RadioButton
                            val rbcredito =
                                promptView.findViewById<View>(R.id.creditBill) as RadioButton

                            if (creditoTime == 0) {
                                rbcredito.visibility = View.GONE
                            }

                            alertDialogBuilder
                                .setCancelable(true)
                                .setPositiveButton(
                                    "OK"
                                ) { dialog, id ->
                                    fecha =
                                        date + " " + get24Time()
                                    if (!rbcontado.isChecked && !rbcredito.isChecked) {
                                        CreateMessage(
                                            QuickContext!!,
                                            " ",
                                            "Debe seleccionar una opción"
                                        )
                                    } else {
                                        if (rbcredito.isChecked) {
                                            txtObservaciones!!.setText(
                                                " "
                                            )
                                            observ =
                                                txtObservaciones!!.text.toString()
                                            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                                            metodoPagoId = "2"
                                            notifyDataSetChanged()
                                            agregar()
                                            tabCliente = 1
                                            activity.selecClienteTabVentaDirecta = tabCliente
                                            activity.creditoLimiteClienteVentaDirecta =
                                                creditoLimiteClienteP
                                            activity.dueClienteVentaDirecta = dueClienteP

                                            //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                            activity.setInvoiceIdPreventa(nextId)
                                            activity.metodoPagoClienteVentaDirecta =
                                                metodoPagoId


                                            /*final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando",
                                                                                                                                    "Seleccionando Cliente", true);*/
                                            val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                                                                                                            "Seleccionando Cliente", true);*/
                                            val message = "Seleccionando Cliente"
                                            val titulo = "Cargando"
                                            val spannableString = SpannableString(message)
                                            val spannableStringTitulo = SpannableString(titulo)

                                            val typefaceSpan = CalligraphyTypefaceSpan(
                                                TypefaceUtils.load(
                                                    QuickContext!!.assets,
                                                    "font/monse.otf"
                                                )
                                            )
                                            spannableString.setSpan(
                                                typefaceSpan,
                                                0,
                                                message.length,
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                            )
                                            spannableStringTitulo.setSpan(
                                                typefaceSpan,
                                                0,
                                                titulo.length,
                                                Spanned.SPAN_PRIORITY
                                            )

                                            progresRing.setTitle(spannableStringTitulo)
                                            progresRing.setMessage(spannableString)
                                            progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                                            progresRing.isIndeterminate = true
                                            progresRing.setCancelable(true)
                                            progresRing.show()
                                            progresRing.setCancelable(true)
                                            Thread {
                                                try {
                                                    Thread.sleep(5000)
                                                } catch (e: Exception) {
                                                }
                                                progresRing.dismiss()
                                            }.start()
                                            actualizarClienteVisitado()
                                        } else if (rbcontado.isChecked) {
                                            txtObservaciones!!.setText(
                                                " "
                                            )
                                            observ =
                                                txtObservaciones!!.text.toString()
                                            metodoPagoId = "1"
                                            notifyDataSetChanged()
                                            agregar()
                                            tabCliente = 1
                                            activity.selecClienteTabVentaDirecta = tabCliente
                                            activity.creditoLimiteClienteVentaDirecta =
                                                creditoLimiteClienteP
                                            activity.dueClienteVentaDirecta = dueClienteP

                                            //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                            activity.setInvoiceIdPreventa(nextId)
                                            activity.metodoPagoClienteVentaDirecta =
                                                metodoPagoId


                                            /* final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                                                                                                                            progresRing.setCancelable(true);*/
                                            val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                                                                                                            "Seleccionando Cliente", true);*/
                                            val message = "Seleccionando Cliente"
                                            val titulo = "Cargando"
                                            val spannableString = SpannableString(message)
                                            val spannableStringTitulo = SpannableString(titulo)

                                            val typefaceSpan = CalligraphyTypefaceSpan(
                                                TypefaceUtils.load(
                                                    QuickContext!!.assets,
                                                    "font/monse.otf"
                                                )
                                            )
                                            spannableString.setSpan(
                                                typefaceSpan,
                                                0,
                                                message.length,
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                            )
                                            spannableStringTitulo.setSpan(
                                                typefaceSpan,
                                                0,
                                                titulo.length,
                                                Spanned.SPAN_PRIORITY
                                            )

                                            progresRing.setTitle(spannableStringTitulo)
                                            progresRing.setMessage(spannableString)
                                            progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                                            progresRing.isIndeterminate = true
                                            progresRing.setCancelable(true)
                                            progresRing.show()
                                            Thread {
                                                try {
                                                    Thread.sleep(5000)
                                                } catch (e: Exception) {
                                                }
                                                progresRing.dismiss()
                                            }.start()
                                            actualizarClienteVisitado()
                                        }
                                        val nextId2: Int
                                        val realm5 = Realm.getDefaultInstance()

                                        realm5.beginTransaction()
                                        val currentIdNum = realm5.where(
                                            Pivot::class.java
                                        ).max("id")


                                        nextId2 = currentIdNum?.toInt() ?: 0

                                        session.guardarDatosPivotVentaDirecta(nextId2)
                                        Log.d("currentIdNum", "" + nextId2)
                                        realm5.commitTransaction()
                                        realm5.close()
                                    }
                                }
                                .setNegativeButton(
                                    "Cancel"
                                ) { dialog, id -> dialog.cancel() }


                            val alertSeg = alertDialogBuilder.create()
                            alertSeg.show()
                        }
                        btnCancel.setOnClickListener { dialogInicial.dismiss() }
                    }.setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.cancel() }.create()
                dialogReturnSale.show()
            }
        })

        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    fun setFilter(countryModels: List<Clientes>) {
        contentList = ArrayList()
        contentList.addAll(countryModels)
        notifyDataSetChanged()
    }

    fun agregar() {
        val realm = Realm.getDefaultInstance()
        usuer = session.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        idUsuario = usuarios!!.id


        if (feCliente == "0") {
            idTypeInvoice = "04"
        } else if (feCliente == "1") {
            idTypeInvoice = "01"
        }

        realm.close()

        val realm2 = Realm.getDefaultInstance()

        realm2.executeTransaction { realm -> // increment index
            /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();
    
                                        if(numeracion.getId()){}
    */

            // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

            val numero = realm.where(Numeracion::class.java).equalTo("sale_type", "1")
                .max("number")

            nextId = if (numero == null) {
                1
            } else {
                numero.toInt() + 1
            }
            val valor = numero!!.toInt()

            val length = valor.toString().length
            if (length == 1) {
                numFactura = idUsuario + "01-" + "000000" + nextId
            } else if (length == 2) {
                numFactura = idUsuario + "01-" + "00000" + nextId
            } else if (length == 3) {
                numFactura = idUsuario + "01-" + "0000" + nextId
            } else if (length == 4) {
                numFactura = idUsuario + "01-" + "000" + nextId
            } else if (length == 5) {
                numFactura = idUsuario + "01-" + "00" + nextId
            } else if (length == 6) {
                numFactura = idUsuario + "01-" + "0" + nextId
            } else if (length == 7) {
                numFactura = idUsuario + "01-" + nextId
            }
        }

        //TODO MODIFICAR CON EL IDS CONSECUTIVOS (FACTURA Y NUMERACION)
        activity.initCurrentInvoice(
            nextId.toString(),
            idTypeInvoice,
            "3",
            numFactura,
            "",
            "",
            0.0,
            0.0,
            date,
            get24Time(),
            date,
            get24Time(),
            date,
            "1",
            metodoPagoId,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            fecha,
            "",
            "",
            1,
            0
        )

        activity.initCurrentVenta(
            nextId.toString(),
            nextId.toString(),
            idCliente,
            " ",
            "6",
            "2",
            "0",
            "0",
            fecha,
            fecha,
            "0",
            1,
            1,
            "VentaDirecta"
        )

        val realm5 = Realm.getDefaultInstance()
        realm5.executeTransaction { realm5 ->
            val numNuevo = Numeracion() // unmanaged
            numNuevo.sale_type = "1"
            numNuevo.numeracion_numero = nextId
            numNuevo.setRec_creada(1)
            realm5.insertOrUpdate(numNuevo)
            Log.d("idinvNUEVOCREADO", numNuevo.toString() + "")
        }
        realm5.close()
        // realm2.close();
    }

    fun obtenerLocalización() {
        gps = GPSTracker(activity)

        if (gps!!.canGetLocation()) {
            latitude = gps!!.getLatitude()
            longitude = gps!!.getLongitude()
        } else {
            gps!!.showSettingsAlert()
        }
    }

    protected fun actualizarClienteVisitado() {
        val realm = Realm.getDefaultInstance()
        usuer = session.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        val idUsuario = usuarios!!.id

        //    String clienteid = activity.getCurrentVenta().getCustomer_id();

        /* sale sale = realm.where(sale.class).equalTo("invoice_id", String.valueOf(nextId)).findFirst();
        String clienteid = sale.getCustomer_id();*/
        Log.d("ClienteVisitadoFact", nextId.toString() + "")
        Log.d("ClienteVisitadoClient", idCliente + "")
        realm.close()

        val realm5 = Realm.getDefaultInstance()

        realm5.beginTransaction()
        val currentIdNum = realm5.where(visit::class.java).max("id")

        nextId = if (currentIdNum == null) {
            1
        } else {
            currentIdNum.toInt() + 1
        }


        val visitadonuevo = visit()

        visitadonuevo.id = nextId
        visitadonuevo.customer_id = idCliente
        visitadonuevo.visit = seleccion
        visitadonuevo.observation = observ
        visitadonuevo.date = date
        visitadonuevo.longitud = longitude
        visitadonuevo.latitud = latitude
        visitadonuevo.user_id = idUsuario
        visitadonuevo.subida = 1
        visitadonuevo.tipoVisitado = "VD"

        realm5.copyToRealmOrUpdate(visitadonuevo)
        realm5.commitTransaction()
        Log.d("ClienteVisitado", visitadonuevo.toString() + "")
        realm5.close()
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    fun devolverTodo() {
        facturaid1 =
            activity.allPivotDelegate //realm.where(Pivot.class).equalTo("invoice_id", idFacturaSeleccionada).findAll();
        Log.d("PRODUCTOSFACTURAS", facturaid1.toString() + "")

        for (i in facturaid1!!.indices) {
            val eventRealm = facturaid1!![i]
            val cantidadDevolver = eventRealm.amount!!.toDouble()

            Log.d("PRODUCTOSFACTURASEPA1", eventRealm.toString() + "")
            Log.d("PRODUCTOSFACTURASEPA", cantidadDevolver.toString() + "")

            val resumenProductoId = eventRealm.id
            Log.d("resumenProductoId", resumenProductoId.toString() + "")

            // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                val inventario = realm3.where(Inventario::class.java)
                    .equalTo("product_id", eventRealm.product_id).findFirst()
                if (inventario != null) {
                    idInvetarioSelec = inventario.id
                    amount_inventario = inventario.amount!!.toDouble()
                    Log.d("idinventario", idInvetarioSelec.toString() + "")
                } else {
                    amount_inventario = 0.0
                    // increment index
                    val currentIdNum = realm3.where(Inventario::class.java).max("id")

                    nextId = if (currentIdNum == null) {
                        1
                    } else {
                        currentIdNum.toInt() + 1
                    }

                    val invnuevo = Inventario() // unmanaged
                    invnuevo.id = nextId
                    invnuevo.product_id = eventRealm.product_id
                    invnuevo.initial = "0".toString()
                    invnuevo.amount = cantidadDevolver.toString()
                    invnuevo.amount_dist = "0".toString()
                    invnuevo.distributor = "0".toString()

                    realm3.insertOrUpdate(invnuevo)
                    Log.d("idinvNUEVOCREADO", invnuevo.toString() + "")
                }
                realm3.close()
            }


            // OBTENER NUEVO AMOUNT_DIST
            val nuevoAmountDevuelto = cantidadDevolver + amount_inventario
            Log.d("nuevoAmount", nuevoAmountDevuelto.toString() + "")

            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
            val realm2 = Realm.getDefaultInstance()
            realm2.executeTransaction { realm2 ->
                val inv_actualizado =
                    realm2.where(Inventario::class.java).equalTo("id", idInvetarioSelec)
                        .findFirst()
                inv_actualizado!!.amount = nuevoAmountDevuelto.toString()
                realm2.insertOrUpdate(inv_actualizado)
                realm2.close()
            }
            activity.borrarProduct(eventRealm)
        }

        session.guardarDatosBloquearBotonesDevolver(0)
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_prev_card: TextView = view.findViewById<View>(R.id.txt_ventadir_card) as TextView
        val txt_prev_fantasyname: TextView =
            view.findViewById<View>(R.id.txt_ventadir_fantasyname) as TextView
        val txt_prev_companyname: TextView =
            view.findViewById<View>(R.id.txt_ventadir_companyname) as TextView
        val txt_prev_creditlimit: TextView =
            view.findViewById<View>(R.id.txt_ventadir_creditlimit) as TextView
        val txt_prev_fixeddescount: TextView =
            view.findViewById<View>(R.id.txt_ventadir_fixeddescount) as TextView
        val txt_prev_due: TextView = view.findViewById<View>(R.id.txt_ventadir_due) as TextView
        val txt_prev_credittime: TextView =
            view.findViewById<View>(R.id.txt_ventadir_credittime) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewVentaDirectaClientes) as CardView
    }

    /* private int getContador() {
         Realm realm = Realm.getDefaultInstance();
         realmResultado = realm.where(InvoiceDetallePreventa.class).findAll();
         realmResultado.sort("p_id");

 //        InvoiceDetallePreventa invoiceDetallePreventa = activity.getCurrentInvoice();
 //        invoiceDetallePreventa.setP_code(weqweq);
 //
         return realmResultado.size();
     }
 */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var QuickContext: Context? = null
        private var txtObservaciones: EditText? = null
        private var creditolimite = 0.0
        private var descuentoFixed = 0.0
        private var cleintedue = 0.0
        private var credittime = 0.0
    }
}

