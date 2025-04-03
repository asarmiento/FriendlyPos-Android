package com.friendlypos.Recibos.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.Recibos.activity.RecibosActivity
import com.friendlypos.Recibos.adapters.RecibosSeleccionarFacturaAdapter
import com.friendlypos.Recibos.modelo.recibos
import com.friendlypos.Recibos.util.TotalizeHelperRecibos
import com.friendlypos.distribucion.fragment.BaseFragment
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class RecibosSeleccionarFacturaFragment : BaseFragment() {
    private val realm: Realm? = null
    var recyclerView: RecyclerView? = null
    private var adapter: RecibosSeleccionarFacturaAdapter? = null
    var totalizeHelper: TotalizeHelperRecibos? = null
    var slecTAB: Int = 0
    var activity: RecibosActivity? = null
    var txtPagoTotal: TextView? = null
    var txtPagoCancelado: TextView? = null
    var debePagar: Double = 0.0
    var sb: StringBuffer? = null
    var cantidadPagar: Double = 0.0

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as RecibosActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(
            R.layout.fragment_recibos_seleccionar_factura, container,
            false
        )
        setHasOptionsMenu(true)

        txtPagoTotal = rootView.findViewById<View>(R.id.txtPagoTotal) as TextView
        txtPagoCancelado = rootView.findViewById<View>(R.id.txtPagoCancelado) as TextView
        totalizeHelper = TotalizeHelperRecibos(activity)
        applyBill = rootView.findViewById<View>(R.id.btnPagoTotal) as Button
        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewRecibosSeleccFactura) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        recyclerView!!.setHasFixedSize(true)
        slecTAB = activity!!.selecClienteTabRecibos
        if (adapter == null) {
            adapter = RecibosSeleccionarFacturaAdapter(
                activity!!, this,
                listProductos
            )
        }
        if (slecTAB == 1) {
            val list = listProductos
            activity!!.cleanTotalize()
            totalizeHelper!!.totalizeRecibos(list)
            Log.d("listaResumen", list.toString() + "")
        }

        recyclerView!!.adapter = adapter

        applyBill!!.setOnClickListener {
            sb = StringBuffer()
            for (r in adapter!!.checked) {
                sb!!.append(r.numeration)

                val total = r.total
                val pago = r.paid
                debePagar = total - pago
                activity!!.setTotalizarTotalCheck(debePagar)
            }

            if (adapter!!.checked.size > 0) {
                val totalCheck = activity!!.getTotalizarTotalCheck()

                Toast.makeText(activity, totalCheck.toString() + "", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "no hay", Toast.LENGTH_LONG).show()
            }

            //double totalT = activity.getTotalizarTotal();
            val totalP = activity!!.getTotalizarCancelado()
            Log.d("totalRecibos", "" + totalP)

            val layoutInflater = LayoutInflater.from(activity)
            val promptView = layoutInflater.inflate(R.layout.promptrecibospagototal, null)

            val alertDialogBuilder = AlertDialog.Builder(
                activity!!
            )
            alertDialogBuilder.setView(promptView)

            val label =
                promptView.findViewById<View>(R.id.promtClabelRecibosPagoTotal) as TextView
            label.text = "Escriba un pago maximo de " + String.format(
                "%,.2f",
                totalP
            ) + " minima de 1"

            val input =
                promptView.findViewById<View>(R.id.promtCtextRecibosPagoTotal) as EditText

            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton(
                "OK"
            ) { dialog, id ->
                var numIngresado = input.text.toString().toDouble()
                if (!input.text.toString().isEmpty()) {
                    if (numIngresado > totalP) {
                        numIngresado = totalP
                        Toast.makeText(
                            getActivity(),
                            "Ajusto $cantidadPagar $debePagar ", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            getActivity(),
                            "no ajusto $cantidadPagar $debePagar ", Toast.LENGTH_LONG
                        ).show()
                    }


                    if (numIngresado > totalP) {
                        Toast.makeText(
                            getActivity(),
                            "Ingrese una cantidad menor al total",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        cantidadPagar = numIngresado
                        Log.d(
                            "cantidadPagar1",
                            "" + String.format("%,.2f", cantidadPagar)
                        )
                        val dialogReturnSale = AlertDialog.Builder(
                            activity!!
                        )
                            .setTitle("Pago total")
                            .setMessage("¿Desea proceder con el pago de las facturas?")
                            .setPositiveButton(
                                "OK"
                            ) { dialog, which ->
                                try {
                                    val clienteId = activity!!.clienteIdRecibos
                                    val realm = Realm.getDefaultInstance()
                                    val result: RealmResults<recibos> = realm.where<recibos>(
                                        recibos::class.java
                                    ).equalTo("customer_id", clienteId)
                                        .findAllSorted("date", Sort.DESCENDING)

                                    if (result.isEmpty()) {
                                        Toast.makeText(
                                            getActivity(),
                                            "No hay recibos emitidos",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        for (i in result.indices) {
                                            Log.d(
                                                "cantidadPagarfor",
                                                "" + String.format(
                                                    "%,.2f",
                                                    cantidadPagar
                                                )
                                            )
                                            val salesList1: List<recibos> =
                                                realm.where<recibos>(
                                                    recibos::class.java
                                                ).equalTo("customer_id", clienteId)
                                                    .findAllSorted(
                                                        "date",
                                                        Sort.DESCENDING
                                                    )
                                            val totalFactura = salesList1[i].total
                                            val totalPagado = salesList1[i].paid
                                            val facturaId = salesList1[i].invoice_id
                                            Log.d(
                                                "totalFactura",
                                                "" + totalFactura
                                            )
                                            Log.d("totalPagado", "" + totalPagado)


                                            if (totalFactura == totalPagado) {
                                                Log.d("ya", "" + "ya")
                                            } else {
                                                val restante = totalFactura - totalPagado

                                                Log.d(
                                                    "restante",
                                                    "" + String.format("%,.2f", restante)
                                                )

                                                var cantidadPagarRestante = 0.0

                                                Log.d(
                                                    "restante",
                                                    "" + cantidadPagar
                                                )
                                                val irPagando32 = cantidadPagar

                                                if (cantidadPagar >= restante) {
                                                    cantidadPagarRestante =
                                                        cantidadPagar - restante
                                                    cantidadPagar = cantidadPagarRestante

                                                    Log.d(
                                                        "cantidadPagar2",
                                                        "" + String.format(
                                                            "%,.2f",
                                                            cantidadPagar
                                                        )
                                                    )


                                                    //  double cantidadPagarRestanteS = activity.getMontoAgregadoRestante();
                                                    val irPagando = restante + totalPagado

                                                    val realm2 =
                                                        Realm.getDefaultInstance()
                                                    realm2.executeTransaction { realm2 ->
                                                        val recibo_actualizado =
                                                            realm2.where(
                                                                recibos::class.java
                                                            ).equalTo(
                                                                "invoice_id",
                                                                facturaId
                                                            ).findFirst()
                                                        recibo_actualizado!!.paid =
                                                            irPagando
                                                        recibo_actualizado.abonado = 1
                                                        recibo_actualizado.montoCanceladoPorFactura =
                                                            irPagando
                                                        val cant =
                                                            recibo_actualizado.montoCancelado
                                                        if (cant == 0.0) {
                                                            recibo_actualizado.montoCancelado =
                                                                irPagando
                                                        } else {
                                                            recibo_actualizado.montoCancelado =
                                                                cant + irPagando
                                                        }

                                                        realm2.insertOrUpdate(
                                                            recibo_actualizado
                                                        )
                                                        realm2.close()
                                                        Log.d(
                                                            "NuevoPagando",
                                                            recibo_actualizado.toString() + ""
                                                        )
                                                    }
                                                    updateData()
                                                    Log.d(
                                                        "irPagando",
                                                        "" + String.format(
                                                            "%,.2f",
                                                            irPagando
                                                        )
                                                    )
                                                } else {
                                                    if (cantidadPagar < 0.0) {
                                                        Log.d("nohay", "no hay")
                                                    } else {
                                                        var cantidadPagarRestante1 = 0.0
                                                        cantidadPagarRestante1 =
                                                            cantidadPagar - restante
                                                        cantidadPagar = cantidadPagarRestante1


                                                        //  double cantidadPagarRestanteS = activity.getMontoAgregadoRestante();
                                                        val irPagando = restante + totalPagado

                                                        val realm2 =
                                                            Realm.getDefaultInstance()
                                                        realm2.executeTransaction { realm2 ->
                                                            val recibo_actualizado =
                                                                realm2.where(
                                                                    recibos::class.java
                                                                ).equalTo(
                                                                    "invoice_id",
                                                                    facturaId
                                                                ).findFirst()
                                                            recibo_actualizado!!.paid =
                                                                irPagando
                                                            recibo_actualizado.abonado =
                                                                1
                                                            recibo_actualizado.montoCanceladoPorFactura =
                                                                irPagando
                                                            val cant =
                                                                recibo_actualizado.montoCancelado
                                                            if (cant == 0.0) {
                                                                recibo_actualizado.montoCancelado =
                                                                    irPagando
                                                            } else {
                                                                recibo_actualizado.montoCancelado =
                                                                    cant + irPagando
                                                            }

                                                            realm2.insertOrUpdate(
                                                                recibo_actualizado
                                                            )
                                                            realm2.close()
                                                            Log.d(
                                                                "NuevoPagando",
                                                                recibo_actualizado.toString() + ""
                                                            )
                                                        }

                                                        /*   double cantidadPagarRestante1 = 0.0;
                                               final double irPagando332 = cantidadPagar;
                                               cantidadPagarRestante1 = cantidadPagar-  restante;
                                               cantidadPagar = 0.0;
                                               cantidadPagar = cantidadPagarRestante1;
                                            
                                            
                                            
                                               double resultado = cantidadPagar;
                                               double resultado1 = restante;
                                            
                                               double resultado3 = resultado - restante;
                                            
                                               Log.d("resultado3", "" + String.format("%,.2f", resultado3));*/
                                                        updateData()

                                                        Log.d(
                                                            "irPagando",
                                                            "" + String.format(
                                                                "%,.2f",
                                                                irPagando
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        realm.close()
                                        Toast.makeText(
                                            activity,
                                            "Se realizó el pago total",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(getActivity(), e.message, Toast.LENGTH_LONG)
                                        .show()
                                    e.printStackTrace()
                                }
                            }.setNegativeButton(
                                "Cancel"
                            ) { dialog, which -> dialog.cancel() }.create()
                        dialogReturnSale.show()
                    }
                } else {
                    input.error = "Campo requerido"
                    input.requestFocus()
                }
            }
            alertDialogBuilder.setNegativeButton(
                "Cancel"
            ) { dialog, id ->
                dialog.cancel()
                activity!!.cleanTotalizeCkeck()
            }

            val alertD = alertDialogBuilder.create()
            alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            alertD.show()
        }
        return rootView
    }


    private val listProductos: List<recibos>
        get() {
            val clienteId = activity!!.clienteIdRecibos

            val realm = Realm.getDefaultInstance()
            val result1: RealmResults<recibos> =
                realm.where<recibos>(recibos::class.java).equalTo("customer_id", clienteId)
                    .findAllSorted("date", Sort.DESCENDING)
            realm.close()
            Log.d("RECIBOSCLIENTE", result1.toString() + "")
            return result1
        }

    override fun onDestroyView() {
        super.onDestroyView()
        // realm.close();
    }


    override fun updateData() {
        slecTAB = activity!!.selecClienteTabRecibos
        if (slecTAB == 1) {
            activity!!.cleanTotalize()
            val list = listProductos

            adapter!!.updateData(list)
            totalizeHelper!!.totalizeRecibos(list)

            val totalT = activity!!.getTotalizarTotal()
            val totalP = activity!!.getTotalizarCancelado()

            txtPagoTotal!!.text = "Total de todas: " + String.format("%,.2f", totalT)
            txtPagoCancelado!!.text = "Total por pagar: " + String.format("%,.2f", totalP)
            Log.d("totalFull", totalT.toString() + "")
        } else {
            Log.d("SelecUpdateResumen", "No hay productos")
        }
    }

    companion object {
        private var applyBill: Button? = null
        val instance: RecibosSeleccionarFacturaFragment
            get() = RecibosSeleccionarFacturaFragment()
    }
}
