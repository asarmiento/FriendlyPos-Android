package com.friendlysystemgroup.friendlypos.Recibos.adapters

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.application.util.Functions.date
import com.friendlysystemgroup.friendlypos.databinding.ListaRecibosClientesBinding
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
    private val context: Context,
    private val activity: RecibosActivity,
    var contentList: List<recibos>
) : RecyclerView.Adapter<RecibosClientesAdapter.CharacterViewHolder>() {
    private var selected_position = -1
    var facturaID: String? = null
    var clienteID: String? = null
    var tabCliente: Int = 0
    private var usuer: String? = null
    private val session: SessionPrefes = SessionPrefes(context)
    private var idUsuario: String? = null
    private var nextId: Int = 0
    private var numFactura: String? = null

    init {
        QuickContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ListaRecibosClientesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val recibo = contentList[position]
        activity.cleanTotalizeFinal()
        val realm = Realm.getDefaultInstance()

        val clientes = realm.where(Clientes::class.java)
            .equalTo("id", recibo.customer_id)
            .findFirst()

        val result1: RealmResults<recibos> = realm.where(recibos::class.java)
            .equalTo("customer_id", recibo.customer_id)
            .sort("date", Sort.DESCENDING)
            .findAll()

        val cant = result1.size
        Log.d("RECIBOSCLIENTE", cant.toString())

        for (i in 0 until cant) {
            result1[i]?.let { abonado1 ->
                val total1 = abonado1.total
                val pago1 = abonado1.paid
                val totalPagado1 = total1 - pago1

                Log.d("PAGOSFOR2", "$totalPagado1   ${abonado1.abonado}")
                activity.setTotalizarFinalCliente(totalPagado1)
            }
        }

        val tot = activity.getTotalizarFinalCliente()
        Log.d("PAGOSFORTOT", tot.toString())

        if (tot == 0.0) {
            holder.binding.cardViewRecibosClientes.visibility = View.GONE
            holder.binding.cardViewRecibosClientes.layoutParams.height = 0
            val layoutParams = holder.binding.cardViewRecibosClientes.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            holder.binding.cardViewRecibosClientes.requestLayout()
            Log.d("inactivo", "inactivo")
        } else {
            clientes?.let { cliente ->
                holder.binding.txtClienteFacturaCardRecibos.text = cliente.card
                holder.binding.txtClienteFacturaFantasynameRecibos.text = cliente.fantasyName
                holder.binding.txtClienteFacturaCompanynameRecibos.text = cliente.companyName
            }
        }

        holder.binding.cardViewRecibosClientes.setOnClickListener {
            val tabCliente1 = activity.selecClienteTabRecibos
            if (tabCliente1 == 1) {
                AlertDialog.Builder(activity)
                    .setTitle("Salir")
                    .setMessage("¿Desea cancelar la factura en proceso y empezar otra?")
                    .setPositiveButton("OK") { _, _ ->
                        val realm2 = Realm.getDefaultInstance()
                        realm2.executeTransaction { r ->
                            val numero = r.where(Numeracion::class.java)
                                .equalTo("sale_type", "4").max("number")
                            nextId = numero?.toInt()?.minus(1) ?: 1
                        }
                        
                        val realm5 = Realm.getDefaultInstance()
                        realm5.executeTransaction { r ->
                            val numNuevo = Numeracion()
                            numNuevo.sale_type = "4"
                            numNuevo.numeracion_numero = nextId

                            r.insertOrUpdate(numNuevo)
                            Log.d("RecNumNuevaAtras", numNuevo.toString())
                        }

                        val id = nextId + 1
                        val idRecipiente = id.toString()
                        val realm6 = Realm.getDefaultInstance()
                        realm6.executeTransaction { r ->
                            val result = r.where(receipts::class.java)
                                .equalTo("receipts_id", idRecipiente)
                                .findAll()
                            result.deleteAllFromRealm()
                            Log.d("ReciboBorrado", result.toString())
                        }
                        realm5.close()
                        realm6.close()

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
                                // No action needed
                            }
                            progresRing.dismiss()
                        }.start()

                        val pos = holder.adapterPosition
                        if (pos == RecyclerView.NO_POSITION) return@setPositiveButton

                        notifyItemChanged(selected_position)
                        selected_position = pos
                        notifyItemChanged(selected_position)
                        val clickedDataItem = contentList[pos]

                        facturaID = clickedDataItem.invoice_id
                        clienteID = clickedDataItem.customer_id
                        Log.d("totalP", clickedDataItem.porPagar.toString())

                        tabCliente = 1
                        activity.selecClienteTabRecibos = tabCliente
                        activity.clienteIdRecibos = clienteID
                        crearRecibo()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            } else {
                activity.cleanTotalizeFinal()

                // Usar ProgressDialog con estilos personalizados
                val progresRing = ProgressDialog(QuickContext)
                val message = "Seleccionando Cliente"
                val titulo = "Cargando"
                val spannableString = SpannableString(message)
                val spannableStringTitulo = SpannableString(titulo)

                val typefaceSpan = CalligraphyTypefaceSpan(
                    TypefaceUtils.load(
                        QuickContext?.assets, "font/monse.otf"
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
                        // No action needed
                    }
                    progresRing.dismiss()
                }.start()

                val pos = holder.adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                notifyItemChanged(selected_position)
                selected_position = pos
                notifyItemChanged(selected_position)
                val clickedDataItem = contentList[pos]

                facturaID = clickedDataItem.invoice_id
                clienteID = clickedDataItem.customer_id
                Log.d("totalP", clickedDataItem.porPagar.toString())

                tabCliente = 1
                activity.selecClienteTabRecibos = tabCliente
                activity.clienteIdRecibos = clienteID
                crearRecibo()
            }
        }
        
        holder.binding.cardViewRecibosClientes.setBackgroundColor(
            if (selected_position == position) Color.parseColor("#d1d3d4")
            else Color.parseColor("#FFFFFF")
        )
    }

    override fun getItemCount(): Int = contentList.size

    inner class CharacterViewHolder(val binding: ListaRecibosClientesBinding) : 
        RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    private fun crearRecibo() {
        val realm = Realm.getDefaultInstance()
        usuer = session.usuarioPrefs
        val usuarios = realm.where(Usuarios::class.java)
            .equalTo("email", usuer)
            .findFirst()
        idUsuario = usuarios?.id
        realm.close()

        val realm2 = Realm.getDefaultInstance()
        realm2.executeTransaction { r ->
            val numero = r.where(Numeracion::class.java)
                .equalTo("sale_type", "4")
                .max("number")

            nextId = numero?.toInt()?.plus(1) ?: 1
            val valor = numero?.toInt() ?: 0

            numFactura = when (valor.toString().length) {
                1 -> "${idUsuario}04-000000$nextId"
                2 -> "${idUsuario}04-00000$nextId"
                3 -> "${idUsuario}04-0000$nextId"
                4 -> "${idUsuario}04-000$nextId"
                5 -> "${idUsuario}04-00$nextId"
                6 -> "${idUsuario}04-0$nextId"
                else -> "${idUsuario}04-$nextId"
            }
        }

        val realmRecibo = Realm.getDefaultInstance()
        realmRecibo.executeTransaction { r ->
            val receipt = receipts()
            receipt.receipts_id = nextId.toString()
            receipt.customer_id = clienteID
            receipt.reference = numFactura
            receipt.date = date

            r.insertOrUpdate(receipt)
            Log.d("ReciboNuevo", receipt.toString())
            activity.receipts_id_num = nextId.toString()
        }
        realmRecibo.close()

        val realm5 = Realm.getDefaultInstance()
        realm5.executeTransaction { r ->
            val numNuevo = Numeracion()
            numNuevo.sale_type = "4"
            numNuevo.numeracion_numero = nextId
            r.insertOrUpdate(numNuevo)
            Log.d("NumRecibosNueva", numNuevo.toString())
        }
        realm5.close()
    }

    companion object {
        private var QuickContext: Context? = null
    }
}

