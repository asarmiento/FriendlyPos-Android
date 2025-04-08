package com.friendlysystemgroup.friendlypos.reimprimirRecibos.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.util.Functions
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions
import com.friendlysystemgroup.friendlypos.databinding.FragmentReimprimirReciboResumenBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.activity.ReimprimirRecibosActivity
import io.realm.Realm
import io.realm.RealmResults
import com.friendlypos.util.HtmlTextView
import com.friendlypos.util.LocalImageGetter

class ReimprimirReciboResumenFragment : BaseFragment() {
    private var binding: FragmentReimprimirReciboResumenBinding? = null
    private var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    private var activity: ReimprimirRecibosActivity? = null
    private var reciboActualizado: receipts? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver().apply {
            setBluetoothStateChangeReceiver(requireContext())
        }
        activity = getActivity() as? ReimprimirRecibosActivity
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothStateChangeReceiver?.let {
                requireActivity().unregisterReceiver(it)
            }
        } catch (e: Exception) {
            Log.e("ReciboResumen", "Error al desregistrar receptor", e)
        }
        bluetoothStateChangeReceiver = null
        activity = null
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReimprimirReciboResumenBinding.inflate(inflater, container, false)
        return binding?.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }
    
    private fun setupButtons() {
        binding?.btnReimprimirReciboNuevo?.setOnClickListener {
            if (bluetoothStateChangeReceiver?.isBluetoothAvailable == true) {
                showPrintDialog()
            } else {
                Functions.CreateMessage(
                    requireActivity(),
                    "Error",
                    "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                )
            }
        }
    }
    
    private fun showPrintDialog() {
        val layoutInflater = LayoutInflater.from(requireActivity())
        val promptView = layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)
        
        val alertDialogBuilder = AlertDialog.Builder(requireActivity()).apply {
            setView(promptView)
            setCancelable(false)
            setPositiveButton("OK") { _, _ ->
                val input = promptView.findViewById<EditText>(R.id.promtCtextRecibosImp)
                val cantidadImpresiones = input.text.toString()
                
                reciboActualizado?.let { recibo ->
                    PrinterFunctions.imprimirReimpRecibosTotal(
                        recibo,
                        requireActivity(),
                        1,
                        cantidadImpresiones
                    )
                    Toast.makeText(requireActivity(), "Reimprimir recibo", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
        }
        
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertDialog.show()
    }

    private fun renderHtmlPreview() {
        try {
            val realm = Realm.getDefaultInstance()
            try {
                val preview = buildPreviewContent(realm)
                binding?.htmlTextReciboNuevo?.setHtmlFromString(preview, LocalImageGetter())
            } finally {
                realm.close()
            }
        } catch (e: Exception) {
            Log.e("ReciboResumen", "Error al generar vista previa", e)
            val errorMessage = "<center><h2>Error al cargar la vista previa</h2></center>"
            binding?.htmlTextReciboNuevo?.setHtmlFromString(errorMessage, LocalImageGetter())
        }
    }
    
    private fun buildPreviewContent(realm: Realm): String {
        if (reciboActualizado == null) {
            return "<center><h2>Seleccione un recibo para ver el detalle</h2></center>"
        }
        
        val customerId = reciboActualizado?.customer_id
        val cliente = realm.where(Clientes::class.java)
            .equalTo("id", customerId)
            .findFirst()
            
        val nombreCliente = cliente?.fantasyName ?: "Cliente no encontrado"
        
        var preview = "<h5>Recibos</h5>"
        preview += "<a><b>A nombre de:</b> $nombreCliente</a><br><br>"
        preview += "<a>------------------------------------------------<a><br>"
        preview += getReciboDetails(customerId ?: "", realm)
        
        return preview
    }

    private fun getReciboDetails(idCliente: String, realm: Realm): String {
        val reference = reciboActualizado?.reference ?: return "No hay datos de referencia"
        
        val receiptsResult = realm.where(receipts::class.java)
            .equalTo("customer_id", idCliente)
            .equalTo("reference", reference)
            .findAll()
            
        if (receiptsResult.isEmpty()) {
            return "No hay recibos emitidos para este cliente"
        }
        
        val receipt = receiptsResult.first()
        val recibo = realm.where(recibos::class.java)
            .equalTo("numeration", receipt.numeration)
            .findFirst()
            
        // Formatear datos del recibo
        val numeroReferencia = receipt.reference ?: ""
        val numeracion = receipt.numeration ?: ""
        val pagado = String.format("%,.2f", receipt.montoCanceladoPorFactura)
        val total = String.format("%,.2f", recibo?.total ?: 0.0)
        val restante = String.format("%,.2f", receipt.porPagarReceipts)
        val totalAbonos = String.format("%,.2f", receipt.balance)
        
        // Construir HTML con datos formateados
        return buildHtmlForRecibo(
            numeroReferencia,
            numeracion,
            pagado,
            total,
            totalAbonos,
            restante
        )
    }
    
    private fun buildHtmlForRecibo(
        referencia: String,
        numeracion: String,
        pagado: String,
        total: String,
        abonos: String,
        restante: String
    ): String {
        return "<a><b>" + padRight("# Referencia:", 20.0) + "</b></a>" + padRight(referencia, 20.0) + "<br>" +
                "<a><b>" + padRight("# Factura:", 30.0) + "</b></a>" + padRight(numeracion, 20.0) + "<br>" +
                "<a><b>" + padRight("Total Recibo:", 20.0) + "</b></a>" + padRight(pagado, 20.0) + "<br>" +
                "<a><b>" + padRight("Monto total:", 30.0) + "</b></a>" + padRight(total, 20.0) + "<br>" +
                "<a><b>" + padRight("Total en abonos:", 20.0) + "</b></a>" + padRight(abonos, 20.0) + "<br>" +
                "<a><b>" + padRight("Total restante:", 25.0) + "</b></a>" + padRight(restante, 20.0) + "<br>" +
                "<a>------------------------------------------------<a><br>"
    }
    
    private fun padRight(s: String, n: Double): String {
        return String.format("%-${n.toInt()}s", s)
    }

    override fun updateData() {
        val currentActivity = activity as? ReimprimirRecibosActivity
        if (currentActivity?.selecReciboTab == 1) {
            val reciboId = currentActivity.invoiceIdReimprimirRecibo
            
            val realm = Realm.getDefaultInstance()
            try {
                reciboActualizado = realm.where(receipts::class.java)
                    .equalTo("reference", reciboId)
                    .findFirst()?.let { realm.copyFromRealm(it) }
                
                Log.d("ReciboResumen", "ID Cliente: ${reciboActualizado?.customer_id}")
                Log.d("ReciboResumen", "ID Recibo: $reciboId")
                
                renderHtmlPreview()
            } finally {
                realm.close()
            }
        }
    }
    
    companion object {
        fun newInstance(): ReimprimirReciboResumenFragment = ReimprimirReciboResumenFragment()
    }
}


