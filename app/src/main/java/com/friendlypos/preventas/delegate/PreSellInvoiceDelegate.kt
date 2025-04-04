package com.friendlysystemgroup.friendlypos.preventas.delegate

import android.util.Log
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.preventas.activity.PreventaActivity
import com.friendlysystemgroup.friendlypos.preventas.modelo.invoiceDetallePreventa
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.activity.ReimprimirPedidosActivity
import io.realm.RealmList

class PreSellInvoiceDelegate {
    var preventaActivity: PreventaActivity? = null
    var reimprimirPedidosActivity: ReimprimirPedidosActivity? = null
    var currentInvoice: invoiceDetallePreventa? = null
    var currentVenta: sale? = null
    var productofacturas: MutableList<Pivot>

    constructor(preventaActivity: PreventaActivity?) {
        this.preventaActivity = preventaActivity
        productofacturas = ArrayList()
    }

    constructor(reimprimirPedidosActivity: ReimprimirPedidosActivity?) {
        this.reimprimirPedidosActivity = reimprimirPedidosActivity
        productofacturas = ArrayList()
    }

    fun initInvoiceDetallePreventa(
        id: String,
        branch_office_id: String?,
        numeration: String?,
        latitude: Double,
        longitude: Double,
        date: String?,
        times: String?,
        date_presale: String?,
        times_presale: String?,
        due_data: String?,
        invoice_type_id: String?,
        payment_method_id: String?,
        totalSubtotal: String,
        totalGrabado: String,
        totalExento: String,
        totalDescuento: String,
        percent_discount: String?,
        totalImpuesto: String,
        totalTotal: String,
        changing: String?,
        notes: String?,
        canceled: String?,
        paid_up: String?,
        paid: String?,
        created_at: String?,
        idUsuario: String?,
        idUsuarioAplicado: String?
    ) {
        currentInvoice = invoiceDetallePreventa()

        currentInvoice!!.p_id = id.toInt()
        currentInvoice!!.p_branch_office_id = branch_office_id
        currentInvoice!!.p_numeration = numeration
        currentInvoice!!.p_latitud = latitude
        currentInvoice!!.p_longitud = longitude
        currentInvoice!!.p_date = date
        currentInvoice!!.p_times = times
        currentInvoice!!.p_date_presale = date_presale
        currentInvoice!!.p_time_presale = times_presale
        currentInvoice!!.p_due_date = due_data
        currentInvoice!!.p_invoice_type_id = invoice_type_id
        currentInvoice!!.setP_payment_method_id(payment_method_id)
        currentInvoice!!.p_subtotal = totalSubtotal.toString()
        currentInvoice!!.p_subtotal_taxed = totalGrabado.toString()
        currentInvoice!!.p_subtotal_exempt = totalExento.toString()
        currentInvoice!!.p_discount = totalDescuento.toString()
        currentInvoice!!.p_percent_discount = percent_discount
        currentInvoice!!.p_tax = totalImpuesto.toString()
        currentInvoice!!.p_total = totalTotal.toString()
        currentInvoice!!.p_changing = changing
        currentInvoice!!.p_note = notes
        currentInvoice!!.p_canceled = canceled
        currentInvoice!!.p_paid_up = paid_up
        currentInvoice!!.p_paid = paid
        currentInvoice!!.p_created_at = created_at
        currentInvoice!!.p_user_id = idUsuario
        currentInvoice!!.p_user_id_applied = idUsuarioAplicado

        // newInvoice.setP_sale(newSale);
        currentInvoice!!.p_productofacturas = productofacturas


        Log.d("invoice1", currentInvoice.toString() + "")
    }

    fun initVentaDetallesPreventa(
        p_id: String?,
        p_invoice_id: String?,
        p_customer_id: String?,
        p_customer_name: String?,
        p_cash_desk_id: String?,
        p_sale_type: String?,
        p_viewed: String?,
        p_applied: String?,
        p_created_at: String?,
        p_updated_at: String?,
        p_reserved: String?,
        aplicada: Int,
        subida: Int,
        facturaDePreventa: String?
    ) {
        currentVenta = sale()


        /*  newSale.setP_id(p_id);
        newSale.setP_invoice_id(p_invoice_id);
        newSale.setP_customer_id(p_customer_id);
        newSale.setP_customer_name(p_customer_name);
        newSale.setP_cash_desk_id(p_cash_desk_id);
        newSale.setP_sale_type(p_sale_type);
        newSale.setP_viewed(p_viewed);
        newSale.setP_applied(p_applied);
        newSale.setP_created_at(p_created_at);
        newSale.setP_updated_at(p_updated_at);
        newSale.setP_reserved(p_reserved);*/
        currentVenta!!.id = p_id
        currentVenta!!.invoice_id = p_invoice_id
        currentVenta!!.customer_id = p_customer_id
        currentVenta!!.customer_name = p_customer_name
        currentVenta!!.cash_desk_id = p_cash_desk_id
        currentVenta!!.sale_type = p_sale_type
        currentVenta!!.viewed = p_viewed
        currentVenta!!.applied = p_applied
        currentVenta!!.created_at = p_created_at
        currentVenta!!.updated_at = p_updated_at
        currentVenta!!.reserved = p_reserved
        currentVenta!!.aplicada = aplicada
        currentVenta!!.subida = subida
        currentVenta!!.facturaDePreventa = facturaDePreventa


        Log.d("invoiceSale", currentVenta.toString() + "")
    }

    fun insertProduct(pivot: Pivot) {
        productofacturas.add(pivot)

        Log.d("invoice2", productofacturas.toString() + "")
    }


    fun initProduct(pos: Int): List<Pivot> {
        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio")
            // send = "No hay invoice emitidas";
        } else {
            val devolver = productofacturas[pos].devuelvo

            Log.d("invoiceDev", devolver.toString() + "")
            if (devolver == 0) {
                productofacturas[pos].devuelvo = 1
            } else {
                //  productofacturas.get(pos).removeAllChangeListeners();
                Log.d("vacio", "No hay productos")
            }
        }
        Log.d("invoiceremover", productofacturas.toString() + "")
        return productofacturas
    }


    fun borrarProductoPreventa(pivot: Pivot?) {
        for (i in productofacturas.indices) {
            Log.d("invoiceBorrarSize", productofacturas.size.toString() + "")
            productofacturas[i].devuelvo = 1
        }
        Log.d("invoiceBorrar", productofacturas.toString() + "")
    }


    val allPivot: List<Pivot>
        get() {
            if (productofacturas.isEmpty()) {
                Log.d("vacio", "vacio")
                // send = "No hay invoice emitidas";
            } else {
                for (i in productofacturas.indices) {
                    val devolver = productofacturas[i].devuelvo
                    Log.d("invoiceDev", devolver.toString() + "")
                    if (devolver == 0) {
                        productofacturas[i]
                    } else {
                        productofacturas.removeAt(i)
                        Log.d("vacio", "No hay productos")
                    }
                }
            }
            Log.d("invoice4", productofacturas.toString() + "")
            return productofacturas
        }


    fun destroy() {
        this.preventaActivity = null
        this.reimprimirPedidosActivity = null
        this.currentInvoice = null
    }

    val invoiceByInvoiceDetalle: invoice
        get() {
            val invoice = invoice()

            invoice.id = currentInvoice!!.p_id.toString()
            invoice.branch_office_id = currentInvoice!!.p_branch_office_id
            invoice.numeration = currentInvoice!!.p_numeration
            invoice.latitud = currentInvoice!!.p_latitud
            invoice.longitud = currentInvoice!!.p_longitud
            invoice.date = currentInvoice!!.p_date
            invoice.times = currentInvoice!!.p_times
            invoice.date_presale = currentInvoice!!.p_date_presale
            invoice.time_presale = currentInvoice!!.p_time_presale
            invoice.due_date = currentInvoice!!.p_due_date
            invoice.invoice_type_id = currentInvoice!!.p_invoice_type_id
            invoice.payment_method_id = invoiceDetallePreventa.getP_payment_method_id()
            invoice.subtotal = currentInvoice!!.p_subtotal.toString()
            invoice.subtotal_taxed = currentInvoice!!.p_subtotal_taxed.toString()
            invoice.subtotal_exempt = currentInvoice!!.p_subtotal_exempt.toString()
            invoice.discount = currentInvoice!!.p_discount.toString()
            invoice.percent_discount = currentInvoice!!.p_percent_discount
            invoice.tax = currentInvoice!!.p_tax.toString()
            invoice.total = currentInvoice!!.p_total.toString()
            invoice.changing = currentInvoice!!.p_changing
            invoice.note = currentInvoice!!.p_note
            invoice.canceled = currentInvoice!!.p_canceled
            invoice.paid_up = currentInvoice!!.p_paid_up
            invoice.paid = currentInvoice!!.p_paid
            invoice.created_at = currentInvoice!!.p_created_at
            invoice.user_id = currentInvoice!!.p_user_id
            invoice.user_id_applied = currentInvoice!!.p_user_id_applied
            invoice.sale = currentVenta

            val results = RealmList<Pivot>()

            results.addAll(productofacturas)
            invoice.productofactura = results

            invoice.aplicada = 1
            invoice.subida = 1

            invoice.facturaDePreventa = currentInvoice!!.facturaDePreventa

            Log.d("CREAR PREVENTA", invoice.toString() + "")

            return invoice
        }
}
