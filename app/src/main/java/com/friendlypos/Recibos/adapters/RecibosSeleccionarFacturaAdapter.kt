package com.friendlysystemgroup.friendlypos.Recibos.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.fragments.RecibosSeleccionarFacturaFragment
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.Recibos.util.ItemClickListener
import io.realm.Realm

/**
 * Created by DelvoM on 13/09/2018.
 */
class RecibosSeleccionarFacturaAdapter(
    private val activity: RecibosActivity,
    private val fragment: RecibosSeleccionarFacturaFragment,
    var productosList: List<recibos>
) :
    RecyclerView.Adapter<RecibosSeleccionarFacturaAdapter.CharacterViewHolder>() {
    private var context: Context? = null
    @JvmField
    var checked: ArrayList<recibos> = ArrayList()
    private var selected_position = -1
    var totalPago: Double = 0.0
    var totalPagado: Double = 0.0
    var montoPagar: Double = 0.0
    var debePagar: Double = 0.0
    var montoFaltante: Double = 0.0
    var facturaID: String? = null
    var clienteID: String? = null
    var tabFactura: Int = 0
    var nextId: Int = 0

    fun updateData(productosList: List<recibos>) {
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_recibos_seleccionar_factura, parent, false)
        context = parent.context
        return CharacterViewHolder(view)
    }


    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val inventario = productosList[position]

        val numeracion = inventario.numeration
        val total = inventario.total
        val pago = inventario.paid



        holder.txt_producto_factura_numeracionRecibos.text = "# de factura: $numeracion"

        debePagar = total - pago
        holder.txt_producto_factura_FaltanteRecibos.text =
            "Restante: " + String.format("%,.2f", debePagar)

        holder.txt_producto_factura_FaltanteRecibos.text =
            "Restante: " + String.format("%,.2f", debePagar)
        holder.txt_producto_factura_TotalRecibos.text =
            "Total: " + String.format("%,.2f", total)
        holder.txt_producto_factura_PagoRecibos.text =
            "Pagado: " + String.format("%,.2f", pago)

        holder.fillData(inventario)

        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        if (debePagar == 0.0) {
            holder.cardView.visibility = View.GONE
            holder.cardView.layoutParams.height = 0
            val layoutParams =
                holder.cardView.layoutParams as MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            holder.cardView.requestLayout()
            Log.d("inactivo", "inactivo")
        }
    }

    fun addProduct(facturaID: String?, totalPago: Double, totalPagado: Double, debePagar: Double) {
        val layoutInflater = LayoutInflater.from(context)
        val promptView = layoutInflater.inflate(R.layout.promptrecibos, null)

        val alertDialogBuilder = AlertDialog.Builder(
            context!!
        )
        alertDialogBuilder.setView(promptView)
        val checkbox = promptView.findViewById<View>(R.id.checkbox) as CheckBox

        val label = promptView.findViewById<View>(R.id.promtClabelRecibos) as TextView
        label.text = "Escriba un pago maximo de $debePagar minima de 1"

        val input = promptView.findViewById<View>(R.id.promtCtextRecibos) as EditText

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                input.isEnabled = false
            } else {
                input.isEnabled = true
            }
        }

        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { dialog, id ->
            try {
                montoPagar = if (checkbox.isChecked) {
                    debePagar
                } else {
                    (if (input.text.toString()
                            .isEmpty()
                    ) "0" else input.text.toString()).toDouble()
                }

                val facturaId = activity.invoiceIdRecibos

                if (montoPagar > debePagar) {
                    montoPagar = debePagar
                    //  Toast.makeText(context, "Ajusto " + montoPagar + " " + debePagar + " ", Toast.LENGTH_LONG).show();
                } else {
                    //   Toast.makeText(context, "no ajusto " + montoPagar + " " + debePagar + " ", Toast.LENGTH_LONG).show();
                }

                if (montoPagar <= debePagar) {
                    //   Toast.makeText(context, "Pago " + montoPagar + " " + debePagar + " ", Toast.LENGTH_LONG).show();

                    montoFaltante = totalPagado + montoPagar

                    val realm2 = Realm.getDefaultInstance()
                    realm2.executeTransaction { realm2 ->
                        val recibo_actualizado = realm2.where(recibos::class.java)
                            .equalTo("invoice_id", facturaId).findFirst()
                        recibo_actualizado!!.paid = montoFaltante
                        recibo_actualizado.abonado = 1
                        recibo_actualizado.mostrar = 1
                        recibo_actualizado.montoCanceladoPorFactura = montoPagar
                        val cant = recibo_actualizado.montoCancelado
                        if (cant == 0.0) {
                            recibo_actualizado.montoCancelado = montoPagar
                        } else {
                            recibo_actualizado.montoCancelado = cant + montoPagar
                        }


                        realm2.insertOrUpdate(recibo_actualizado)
                        realm2.close()
                        Log.d("ACT RECIBO", recibo_actualizado.toString() + "")
                    }
                    fragment.updateData()
                    activity.totalizarPagado = montoFaltante
                    activity.montoPagar = montoPagar
                    input.setText(" ")
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "El monto agregado es mayor al monto de la factura",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }

        val alertD = alertDialogBuilder.create()
        alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertD.show()
    }


    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return productosList.size
    }


    override fun getItemViewType(position: Int): Int {
        return 0
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val txt_producto_factura_numeracionRecibos: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_numeracionRecibos) as TextView
        val txt_producto_factura_FaltanteRecibos: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_FaltanteRecibos) as TextView
        val txt_producto_factura_TotalRecibos: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_TotalRecibos) as TextView
        val txt_producto_factura_PagoRecibos: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_PagoRecibos) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewSelecFacRecibos) as CardView


        var itemClickListener: ItemClickListener? = null

        fun fillData(producto: recibos?) {
            cardView.setOnClickListener(View.OnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener

                // Updating old as well as new positions
                notifyItemChanged(selected_position)
                selected_position = adapterPosition
                notifyItemChanged(selected_position)

                val clickedDataItem = productosList[pos]


                facturaID = clickedDataItem.invoice_id
                clienteID = clickedDataItem.customer_id

                totalPago = clickedDataItem.total
                totalPagado = clickedDataItem.paid

                debePagar = totalPago - totalPagado
                Log.d("debePagar", debePagar.toString() + "")

                //    Toast.makeText(activity, facturaID + " " + clienteID + " " + totalPago, Toast.LENGTH_LONG).show();
                activity.totalFacturaSelec = totalPago
                activity.totalizarPagado = totalPagado

                tabFactura = 1
                activity.selecFacturaTabRecibos = tabFactura
                activity.invoiceIdRecibos = facturaID
                addProduct(facturaID, totalPago, totalPagado, debePagar)
            })
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private const val producto_amount_dist_add = 0.0
        private const val producto_descuento_add = 0.0
        var creditoLimiteCliente: Double = 0.0
    }
}