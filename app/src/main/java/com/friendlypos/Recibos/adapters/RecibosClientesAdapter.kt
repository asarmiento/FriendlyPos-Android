package com.friendlysystemgroup.friendlypos.Recibos.adapters

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.application.util.Functions.date
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.preventas.modelo.Numeracion
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

/**
 * Created by DelvoM on 04/09/2018.
 */
class RecibosClientesAdapter(
    context: Context,
    private val activity: RecibosActivity,
    var contentList: List<recibos>
) :
    RecyclerView.Adapter<RecibosClientesAdapter.CharacterViewHolder>() {
    private var selected_position = -1
    var facturaID: String? = null
    var clienteID: String? = null
    var tabCliente: Int = 0
    var usuer: String? = null
    var session: SessionPrefes
    var idUsuario: String? = null
    var nextId: Int = 0
    var numFactura: String? = null

    init {
        QuickContext = context
        session = SessionPrefes(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_recibos_clientes, parent, false)

        return CharacterViewHolder(view)
    }


    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val recibo = contentList[position]
        activity.cleanTotalizeFinal()
        val realm = Realm.getDefaultInstance()

        val clienteId = activity.clienteIdRecibos


        val clientes =
            realm.where(Clientes::class.java).equalTo("id", recibo.customer_id).findFirst()

        // final invoice invoice = realm.where(invoice.class).equalTo("id", recibo.getInvoice_id()).findFirst();
        val cardCliente = clientes!!.card
        val companyCliente = clientes.companyName
        val fantasyCliente = clientes.fantasyName
        val numeracionFactura = recibo.numeration
        var tot = 0.0
        val result1: RealmResults<recibos> =
            realm.where<recibos>(recibos::class.java).equalTo("customer_id", recibo.customer_id)
                .findAllSorted("date", Sort.DESCENDING)

        val cant = result1.size
        Log.d("RECIBOSCLIENTE", cant.toString() + "")

        for (i in 0 until cant) {
            val abonado1 = result1[i]!!.abonado
            val total1 = result1[i]!!.total
            val pago1 = result1[i]!!.paid
            val totalPagado1 = total1 - pago1

            Log.d("PAGOSFOR2", "$totalPagado1   $abonado1")
            activity.setTotalizarFinalCliente(totalPagado1)

            // double porPagar = recibo.getPorPagar();
            val abonado = recibo.abonado
            val total = recibo.total
            val pago = recibo.paid
            val totalPagado = total - pago
            Log.d("PAGOSFOR1", "$totalPagado   $abonado")
        }

        tot = activity.getTotalizarFinalCliente()
        Log.d("PAGOSFORTOT", tot.toString() + "")

        if (tot == 0.0) {
            holder.cardView.visibility = View.GONE
            holder.cardView.layoutParams.height = 0
            val layoutParams =
                holder.cardView.layoutParams as MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            holder.cardView.requestLayout()
            Log.d("inactivo", "inactivo")
        } else {
            holder.txt_cliente_factura_card.text = cardCliente
            holder.txt_cliente_factura_fantasyname.text = fantasyCliente
            holder.txt_cliente_factura_companyname.text = companyCliente
        }



        holder.cardView.setOnClickListener(View.OnClickListener {
            val tabCliente1 = activity.selecClienteTabRecibos
            if (tabCliente1 == 1) {
                val dialogReturnSale = AlertDialog.Builder(activity)
                    .setTitle("Salir")
                    .setMessage("¿Desea cancelar la factura en proceso y empezar otra?")
                    .setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which ->
                            val realm2 = Realm.getDefaultInstance()
                            realm2.executeTransaction { realm ->
                                val numero = realm.where(Numeracion::class.java)
                                    .equalTo("sale_type", "4").max("number")
                                nextId = if (numero == null) {
                                    1
                                } else {
                                    numero.toInt() - 1
                                }
                            }
                            val realm5 = Realm.getDefaultInstance()
                            realm5.executeTransaction { realm5 ->
                                val numNuevo = Numeracion() // unmanaged
                                numNuevo.sale_type = "4"
                                numNuevo.numeracion_numero = nextId

                                realm5.insertOrUpdate(numNuevo)
                                Log.d("RecNumNuevaAtras", numNuevo.toString() + "")
                            }

                            val id = nextId + 1
                            val idRecipiente = id.toString()
                            val realm6 = Realm.getDefaultInstance()
                            realm6.executeTransaction {
                                val result = realm6.where(
                                    receipts::class.java
                                ).equalTo("receipts_id", idRecipiente).findAll()
                                result.deleteAllFromRealm()
                                Log.d("ReciboBorrado", result.toString() + "")
                            }
                            realm5.close()
                            realm6.close()

                            //   activa = 1;
                            activity.cleanTotalizeFinal()
                            val progresRing = ProgressDialog.show(
                                QuickContext,
                                "Cargando",
                                "Seleccionando Cliente",
                                true
                            )
                            progresRing.setCancelable(true)
                            Thread {
                                try {
                                    Thread.sleep(5000)
                                } catch (e: Exception) {
                                }
                                progresRing.dismiss()
                            }.start()

                            val pos = position
                            if (pos == RecyclerView.NO_POSITION) return@OnClickListener

                            notifyItemChanged(selected_position)
                            selected_position = position
                            notifyItemChanged(selected_position)
                            val clickedDataItem = contentList[pos]

                            facturaID = clickedDataItem.invoice_id
                            clienteID = clickedDataItem.customer_id
                            val totalP = clickedDataItem.porPagar

                            Log.d("totalP", totalP.toString() + "")

                            tabCliente = 1
                            activity.selecClienteTabRecibos = tabCliente
                            activity.clienteIdRecibos = clienteID
                            crearRecibo()
                        }).setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.cancel() }.create()
                dialogReturnSale.show()
            } else {
                //   activa = 1;
                activity.cleanTotalizeFinal()


                /*  final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                        progresRing.setCancelable(true);*/
                val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                            "Seleccionando Cliente", true);*/
                val message = "Seleccionando Cliente"
                val titulo = "Cargando"
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

                val pos = position
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener

                notifyItemChanged(selected_position)
                selected_position = position
                notifyItemChanged(selected_position)
                val clickedDataItem = contentList[pos]

                facturaID = clickedDataItem.invoice_id
                clienteID = clickedDataItem.customer_id
                val totalP = clickedDataItem.porPagar

                Log.d("totalP", totalP.toString() + "")

                tabCliente = 1
                activity.selecClienteTabRecibos = tabCliente
                activity.clienteIdRecibos = clienteID
                crearRecibo()
            }
        })
        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_cliente_factura_card: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_cardRecibos) as TextView
        val txt_cliente_factura_fantasyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_fantasynameRecibos) as TextView
        val txt_cliente_factura_companyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_companynameRecibos) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewRecibosClientes) as CardView
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun crearRecibo() {
        val realm = Realm.getDefaultInstance()
        usuer = session.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        idUsuario = usuarios!!.id
        realm.close()

        val realm2 = Realm.getDefaultInstance()

        realm2.executeTransaction { realm -> // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04
            val numero = realm.where(Numeracion::class.java).equalTo("sale_type", "4")
                .max("number")

            nextId = if (numero == null) {
                1
            } else {
                numero.toInt() + 1
            }
            val valor = numero!!.toInt()

            val length = valor.toString().length
            if (length == 1) {
                numFactura = idUsuario + "04-" + "000000" + nextId
            } else if (length == 2) {
                numFactura = idUsuario + "04-" + "00000" + nextId
            } else if (length == 3) {
                numFactura = idUsuario + "04-" + "0000" + nextId
            } else if (length == 4) {
                numFactura = idUsuario + "04-" + "000" + nextId
            } else if (length == 5) {
                numFactura = idUsuario + "04-" + "00" + nextId
            } else if (length == 6) {
                numFactura = idUsuario + "04-" + "0" + nextId
            } else if (length == 7) {
                numFactura = idUsuario + "04-" + nextId
            }
        }


        val realmRecibo = Realm.getDefaultInstance()
        realmRecibo.executeTransaction { realmRecibo ->
            val receipt = receipts() // unmanaged
            receipt.receipts_id = nextId.toString()
            receipt.customer_id = clienteID
            receipt.customer_id = clienteID
            receipt.reference = numFactura
            receipt.date = date

            realmRecibo.insertOrUpdate(receipt)
            Log.d("ReciboNuevo", receipt.toString() + "")
            activity.receipts_id_num = nextId.toString()
        }
        realmRecibo.close()


        val realm5 = Realm.getDefaultInstance()
        realm5.executeTransaction { realm5 ->
            val numNuevo = Numeracion() // unmanaged
            numNuevo.sale_type = "4"
            numNuevo.numeracion_numero = nextId
            realm5.insertOrUpdate(numNuevo)
            Log.d("NumRecibosNueva", numNuevo.toString() + "")
        }
        realm5.close()
    }

    companion object {
        private var QuickContext: Context? = null
    }
}

