package com.friendlypos.reenvio_email.adapters

import android.content.ContentValues
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.reenvio_email.activity.EmailActivity
import com.friendlypos.reenvio_email.fragment.EmailSelecFacturaFragment
import com.friendlypos.reenvio_email.modelo.SendEmailResponse
import com.friendlypos.reenvio_email.modelo.invoices
import com.friendlypos.reenvio_email.modelo.send_email_id

class EmailFacturasAdapter : RecyclerView.Adapter<EmailFacturasAdapter.CharacterViewHolder> {
    private var productosList: List<invoices>? = null
    private var activity: EmailActivity? = null
    private var selected_position1 = -1
    var amount_inventario: Double = 0.0
    var idInvetarioSelec: Int = 0
    var nextId: Int = 0
    var token: String? = null
    private var fragment: EmailSelecFacturaFragment? = null
    private var networkStateChangeReceiver: NetworkStateChangeReceiver? = null
    private var mAPIService: RequestInterface? = null
    var codigoS: String? = null
    var mensajeS: String? = null
    var resultS: String? = null
    var codigo: Int = 0

    constructor(
        activity: EmailActivity?,
        fragment: EmailSelecFacturaFragment?,
        productosList: List<invoices>?
    ) {
        this.productosList = productosList
        this.activity = activity
        this.fragment = fragment
        networkStateChangeReceiver = NetworkStateChangeReceiver()
        mAPIService = api
    }

    constructor()

    fun updateData(productosList: List<invoices>?) {
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_email_facturas, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val pivot = productosList!![position]

        holder.txt_email_factura_numero.text = "Factura: " + pivot.numeration
        holder.txt_email_factura_fecha.text = "Fecha: " + pivot.date
        val pivotTotal = String.format("%,.2f", (pivot.total_voucher))
        holder.txt_email_factura_total.text = "Total: $pivotTotal"
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return productosList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_email_factura_numero: TextView =
            view.findViewById<View>(R.id.txt_email_factura_numero) as TextView
        val txt_email_factura_fecha: TextView =
            view.findViewById<View>(R.id.txt_email_factura_fecha) as TextView
        val txt_email_factura_total: TextView =
            view.findViewById<View>(R.id.txt_email_factura_total) as TextView
        private val txt_resumen_factura_cantidad: TextView? = null
        private val txt_resumen_factura_total: TextView? = null
        protected var cardView: CardView =
            view.findViewById<View>(R.id.cardViewEmailFactura) as CardView
        var btnReenviarFactura: ImageButton =
            view.findViewById<View>(R.id.btnReenviarFactura) as ImageButton

        init {
            btnReenviarFactura.setOnClickListener {
                val dialogReturnSale = AlertDialog.Builder(
                    activity!!
                )
                    .setTitle("Reenviar Email")
                    .setMessage("¿Desea reenviar un email?")
                    .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            val pos = adapterPosition
                            // Updating old as well as new positions
                            notifyItemChanged(selected_position1)
                            selected_position1 = adapterPosition
                            notifyItemChanged(selected_position1)

                            val clickedDataItem = productosList!![pos]
                            val facturaId = clickedDataItem.id
                            Toast.makeText(activity, "Factura #$facturaId", Toast.LENGTH_SHORT)
                                .show()

                            token = "Bearer " + get(activity!!).token
                            Log.d("tokenC", "$token ")


                            if (this.isOnline) {
                                Log.d("factura1", "$facturaId ")

                                val obj = send_email_id(facturaId)
                                Log.d("obj", "$obj ")
                                mAPIService!!.savePostSendEmail(obj, token)
                                    .enqueue(object : retrofit2.Callback<SendEmailResponse?> {
                                        override fun onResponse(
                                            call: retrofit2.Call<SendEmailResponse?>?,
                                            response: retrofit2.Response<SendEmailResponse?>
                                        ) {
                                            if (response.isSuccessful()) {
                                                Log.d(
                                                    "respuestaFactura",
                                                    response.body().toString()
                                                )
                                                codigo = response.code()
                                                Log.d("codigo", codigo.toString() + "")
                                                codigoS = response.body().getCode()
                                                Log.d("codigoS", codigoS + "")
                                                mensajeS = response.body().getMessage()
                                                Log.d("mensajeS", mensajeS + "")
                                                resultS = response.body().isResult().toString()
                                                Log.d("resultS", resultS + "")
                                                Toast.makeText(
                                                    activity,
                                                    mensajeS,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                            }
                                        }


                                        override fun onFailure(
                                            call: retrofit2.Call<SendEmailResponse?>?,
                                            t: Throwable?
                                        ) {
                                            Log.e(
                                                ContentValues.TAG,
                                                "Unable to submit post to API."
                                            )
                                        }
                                    })
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Error, por favor revisar conexión de Internet",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }).setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.cancel() }.create()
                dialogReturnSale.show()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver!!.isNetworkAvailable(activity!!)

    companion object {
        private val aListdata = ArrayList<invoices>()
    }
}
