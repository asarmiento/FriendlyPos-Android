package com.friendlysystemgroup.friendlypos.reimpresion.fragment

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.util.Functions
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlysystemgroup.friendlypos.databinding.FragmentReimprimirResumenBinding
import com.friendlysystemgroup.friendlypos.databinding.PromptImprimirRecibosBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.principal.modelo.Productos
import com.friendlysystemgroup.friendlypos.principal.modelo.Sysconf
import com.friendlysystemgroup.friendlypos.principal.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.principal.util.PrinterFunctions
import com.friendlysystemgroup.friendlypos.reimpresion.activity.ReimprimirActivity
import com.friendlypos.util.LocalImageGetter
import io.realm.Realm
import io.realm.RealmResults
import java.nio.charset.Charset

/**
 * Fragmento para mostrar el resumen de una factura seleccionada para reimprimir
 */
class ReimprimirResumenFragment : BaseFragment() {
    private var _binding: FragmentReimprimirResumenBinding? = null
    private val binding get() = _binding!!
    
    private var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    private var facturaSeleccionada: sale? = null
    private var facturaId: String? = null
    private var nombreMetodoPago: String? = null
    private var tabSeleccionado: Int = 0
    private var tipoFacturacion: String? = null
    
    companion object {
        private const val TAG = "ReimprimirResumenFrag"
        
        /**
         * Crea una nueva instancia del fragmento
         */
        fun newInstance(): ReimprimirResumenFragment = ReimprimirResumenFragment()
        
        /**
         * Formatea texto con relleno para impresión
         */
        private fun padRight(texto: String, espacios: Double): String {
            val pad = (espacios + 4) - texto.length
            
            return if (pad > 0) {
                val relleno = Functions.paddigTabs((pad / 2.0).toInt().toLong())
                "\t$texto\t$relleno"
            } else {
                "\t$texto\t"
            }
        }
        
        /**
         * Obtiene un string formateado con los productos de la factura
         */
        private fun obtenerDetalleProductos(idFactura: String): String {
            val realm = Realm.getDefaultInstance()
            var resultado = ""
            
            try {
                val productos: RealmResults<Pivot> = realm.where(Pivot::class.java)
                    .equalTo("invoice_id", idFactura)
                    .equalTo("devuelvo", 0)
                    .findAll()
                
                if (productos.isEmpty()) {
                    resultado = "No hay productos en esta factura"
                } else {
                    for (pivot in productos) {
                        val producto = realm.where(Productos::class.java)
                            .equalTo("id", pivot.product_id)
                            .findFirst()
                        
                        producto?.let { p ->
                            // Datos del producto
                            val precioSugerido = p.suggested?.toDoubleOrNull() ?: 0.0
                            val descripcion = p.description ?: ""
                            val codigoBarras = p.barcode ?: ""
                            val tipoProducto = p.product_type_id ?: ""
                            
                            // Descripción con codificación correcta
                            val descripcionFormateada = try {
                                val bytes = descripcion.toByteArray(Charset.forName("UTF-8"))
                                String(bytes, charset("UTF-8"))
                            } catch (e: Exception) {
                                descripcion
                            }
                            
                            // Nombre del tipo de producto
                            val nombreTipo = when (tipoProducto) {
                                "1" -> "Gravado"
                                "2" -> "Exento"
                                else -> ""
                            }
                            
                            // Cálculos de precios
                            val cantidad = pivot.amount?.toDoubleOrNull() ?: 0.0
                            val precio = pivot.price?.toDoubleOrNull() ?: 0.0
                            val precioSugeridoCalculado = when (tipoProducto) {
                                "1" -> (precio / 1.13) * (precioSugerido / 100) + (precio * 0.13) + (precio / 1.13)
                                "2" -> precio * (precioSugerido / 100) + precio
                                else -> 0.0
                            }
                            
                            // Formato de impresión
                            resultado += String.format("%s  %.24s ", descripcionFormateada, codigoBarras) + 
                                    "<br>" + String.format("%-12s %-10s %-12s %.10s",
                                    cantidad,
                                    Functions.doubleToString1(precio),
                                    Functions.doubleToString1(precioSugeridoCalculado),
                                    Functions.doubleToString1(cantidad * precio)) + 
                                    "<br>" + String.format("%.10s", nombreTipo) + "<br>"
                            resultado += "<a>------------------------------------------------<a><br>"
                        }
                    }
                }
            } finally {
                realm.close()
            }
            
            return resultado
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar receptor de cambios en Bluetooth
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver().apply {
            setBluetoothStateChangeReceiver(context)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReimprimirResumenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Configurar botón de reimprimir
        binding.btnReimprimirFactura.setOnClickListener {
            onReimprimirButtonClicked()
        }
    }
    
    /**
     * Maneja el click en el botón de reimprimir
     */
    private fun onReimprimirButtonClicked() {
        val isBluetoothActive = bluetoothStateChangeReceiver?.isBluetoothAvailable ?: false
        
        facturaSeleccionada?.let { factura ->
            tipoFacturacion = factura.facturaDePreventa
            
            if (isBluetoothActive) {
                when (tipoFacturacion) {
                    "Distribucion" -> mostrarDialogoImpresion(false)
                    "VentaDirecta" -> mostrarDialogoImpresion(true)
                    else -> mostrarErrorTipoFactura()
                }
            } else {
                mostrarErrorBluetooth()
            }
        } ?: mostrarErrorSeleccionFactura()
    }
    
    /**
     * Muestra diálogo para seleccionar cantidad de impresiones
     */
    private fun mostrarDialogoImpresion(esVentaDirecta: Boolean) {
        val dialogBinding = PromptImprimirRecibosBinding.inflate(layoutInflater)
        
        val alertDialogBuilder = AlertDialog.Builder(requireActivity()).apply {
            setView(dialogBinding.root)
            setCancelable(false)
            
            // Configurar etiqueta
            dialogBinding.promtClabelRecibosImp.text = 
                "Escriba el número de impresiones requeridas"
            
            // Configurar botones
            setPositiveButton("Imprimir") { dialog, _ ->
                val cantidadImpresiones = dialogBinding.promtCtextRecibosImp.text.toString()
                imprimirFactura(esVentaDirecta, cantidadImpresiones)
            }
            
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
        }
        
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertDialog.show()
    }
    
    /**
     * Imprime la factura según el tipo
     */
    private fun imprimirFactura(esVentaDirecta: Boolean, cantidadImpresiones: String) {
        facturaSeleccionada?.let { factura ->
            if (esVentaDirecta) {
                PrinterFunctions.imprimirFacturaVentaDirectaTotal(
                    factura, requireActivity(), 3, cantidadImpresiones
                )
                Toast.makeText(requireContext(), 
                    "Imprimiendo factura de venta directa", 
                    Toast.LENGTH_SHORT).show()
            } else {
                PrinterFunctions.imprimirFacturaDistrTotal(
                    factura, requireActivity(), 1, cantidadImpresiones
                )
                Toast.makeText(requireContext(), 
                    "Imprimiendo factura de distribución", 
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Muestra mensajes de error
     */
    private fun mostrarErrorBluetooth() {
        Functions.CreateMessage(
            requireActivity(),
            "Error de conexión",
            "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
        )
    }
    
    private fun mostrarErrorTipoFactura() {
        Functions.CreateMessage(
            requireActivity(),
            "Error de tipo",
            "Tipo de factura no soportado: $tipoFacturacion"
        )
    }
    
    private fun mostrarErrorSeleccionFactura() {
        Functions.CreateMessage(
            requireActivity(),
            "Error",
            "Debe seleccionar una factura primero"
        )
    }
    
    /**
     * Renderiza la vista previa HTML de la factura
     */
    private fun renderizarVistaPrevia() {
        try {
            val realm = Realm.getDefaultInstance()
            
            try {
                // Obtener información del sistema
                val configuracionSistema = realm.where(Sysconf::class.java).findFirst()
                
                facturaSeleccionada?.let { factura ->
                    // Obtener datos del cliente
                    val cliente = realm.where(Clientes::class.java)
                        .equalTo("id", factura.customer_id)
                        .findFirst()
                    
                    // Obtener datos de la factura
                    val datosFactura = realm.where(invoice::class.java)
                        .equalTo("id", factura.invoice_id)
                        .findFirst()
                    
                    // Productos de la factura
                    val productos = realm.where(Pivot::class.java)
                        .equalTo("invoice_id", factura.invoice_id)
                        .findAll()
                    
                    // Datos del usuario
                    val idUsuario = datosFactura?.user_id ?: ""
                    val usuario = realm.where(Usuarios::class.java)
                        .equalTo("id", idUsuario)
                        .findFirst()
                    
                    // Generar contenido HTML
                    val html = construirContenidoHtml(
                        factura,
                        datosFactura,
                        cliente,
                        configuracionSistema,
                        usuario
                    )
                    
                    // Mostrar en el visor HTML
                    binding.htmlText.setHtmlFromString(html, LocalImageGetter())
                    
                } ?: mostrarMensajeSeleccionFactura()
            } finally {
                realm.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al renderizar vista previa: ${e.message}", e)
            mostrarMensajeError(e)
        }
    }
    
    /**
     * Muestra un mensaje cuando no hay factura seleccionada
     */
    private fun mostrarMensajeSeleccionFactura() {
        val mensaje = "<center><h2>Seleccione una factura para ver su detalle</h2></center>"
        binding.htmlText.setHtmlFromString(mensaje, LocalImageGetter())
    }
    
    /**
     * Muestra un mensaje de error
     */
    private fun mostrarMensajeError(e: Exception) {
        val mensaje = "<center><h2>Error al cargar la factura</h2><p>${e.message}</p></center>"
        binding.htmlText.setHtmlFromString(mensaje, LocalImageGetter())
    }
    
    /**
     * Construye el contenido HTML para la vista previa
     */
    private fun construirContenidoHtml(
        factura: sale,
        datosFactura: invoice?,
        cliente: Clientes?,
        configuracionSistema: Sysconf?,
        usuario: Usuarios?
    ): String {
        // Extraer datos necesarios
        val fechaHora = factura.updated_at ?: ""
        val nombreCliente = factura.customer_name ?: ""
        
        // Datos del cliente
        val idCliente = cliente?.card ?: ""
        val nombreEmpresa = cliente?.companyName ?: ""
        val nombreFantasia = cliente?.fantasyName ?: ""
        
        // Datos de la factura
        val numeroFactura = datosFactura?.numeration ?: ""
        val metodoPago = datosFactura?.payment_method_id ?: ""
        
        // Definir tipo de método de pago
        nombreMetodoPago = when (metodoPago) {
            "1" -> "Contado"
            "2" -> "Crédito"
            else -> "Desconocido"
        }
        
        // Calcular totales
        val totalGravado = Functions.doubleToString1(datosFactura?.subtotal_taxed?.toDoubleOrNull() ?: 0.0)
        val totalExento = Functions.doubleToString1(datosFactura?.subtotal_exempt?.toDoubleOrNull() ?: 0.0)
        val totalSubtotal = Functions.doubleToString1(datosFactura?.subtotal?.toDoubleOrNull() ?: 0.0)
        val totalDescuento = Functions.doubleToString1(datosFactura?.discount?.toDoubleOrNull() ?: 0.0)
        val totalImpuesto = Functions.doubleToString1(datosFactura?.tax?.toDoubleOrNull() ?: 0.0)
        val totalTotal = Functions.doubleToString1(datosFactura?.total?.toDoubleOrNull() ?: 0.0)
        val totalCancelado = Functions.doubleToString1(datosFactura?.paid?.toDoubleOrNull() ?: 0.0)
        val totalVuelto = Functions.doubleToString1(datosFactura?.changing?.toDoubleOrNull() ?: 0.0)
        val notas = datosFactura?.note ?: ""
        
        // Datos del sistema
        val nombreSistema = configuracionSistema?.name ?: ""
        val nombreEmpresaSistema = configuracionSistema?.business_name ?: ""
        val direccionSistema = configuracionSistema?.direction ?: ""
        val identificacionSistema = configuracionSistema?.identification ?: ""
        val telefonoSistema = configuracionSistema?.phone ?: ""
        val correoSistema = configuracionSistema?.email ?: ""
        val nombreUsuario = usuario?.username ?: ""
        
        // Texto legal para facturas a crédito
        val condicionesCredito = """
            Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio.
            El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo.
            El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para
            firmar esta factura, por su representada, conforme al articulo supracitado. Si realiza pago
            mediante transferencia electronica de fondos o cualquier otro medio que no sea efectivo, la
            validez del pago queda sujeto a su acreditacion en las cuentas bancarias de $nombreSistema,
            Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion
        """.trimIndent()
        
        // Determinando tipo de documento
        val tipoDocumento = when (factura.sale_type) {
            "1", "2" -> "Factura"
            "3" -> "Proforma"
            else -> "Factura"
        }
        
        // Construir HTML
        val html = StringBuilder()
        
        // Encabezado
        html.append("<center><h2>$tipoDocumento a $nombreMetodoPago</h2>")
        html.append("<h5>$tipoDocumento #$numeroFactura</h3>")
        html.append("<center><h2>$nombreSistema</h2></center>")
        html.append("<center><h4>$nombreEmpresaSistema</h4></center>")
        html.append("<h6>$direccionSistema</h2></center>")
        html.append("<a><b>Tel:</b> $telefonoSistema</a><br>")
        html.append("<a><b>E-mail:</b> $correoSistema</a><br>")
        html.append("<a><b>Cedula Juridica:</b> $identificacionSistema</a><br>")
        html.append("<a><b>Fecha:</b> $fechaHora</a><br>")
        html.append("<a><b>Fecha de impresión:</b> ${Functions.date} ${Functions.get24Time()}</a><br><br>")
        html.append("<a><b>Vendedor:</b> $nombreUsuario</a><br>")
        html.append("<a><b>ID Cliente:</b> $idCliente</a><br>")
        html.append("<a><b>Cliente:</b> $nombreEmpresa</a><br>")
        html.append("<a><b>A nombre de:</b> $nombreCliente</a><br><br>")
        
        // Encabezados de productos
        html.append("<a><b>${padRight("Descripcion", 10.0)}\t\t${padRight("Codigo", 10.0)}</b></a><br>")
        html.append("<a><b>${padRight("Cantidad", 10.0)}${padRight("Precio", 10.0)}${padRight("P.Sug", 10.0)}${padRight("Total", 10.0)}</b></a><br>")
        html.append("<a><b>${padRight("Tipo", 10.0)}</b></a><br>")
        html.append("<a>------------------------------------------------<a><br>")
        
        // Detalle de productos
        html.append(obtenerDetalleProductos(factura.invoice_id ?: ""))
        
        // Totales
        html.append("<center><a>${String.format("%20s %-20s", "Subtotal Gravado", totalGravado)}</a><br>")
        html.append("<a>${String.format("%20s %-20s", "Subtotal Exento", totalExento)}</a><br>")
        html.append("<a>${String.format("%20s %-20s", "Subtotal", totalSubtotal)}</a><br>")
        html.append("<a>${String.format("%20s %-20s", "IVA", totalImpuesto)}</a><br>")
        html.append("<a>${String.format("%20s %-20s", "Descuento", totalDescuento)}</a><br>")
        html.append("<a>${String.format("%20s %-20s", "Total", totalTotal)}</a><br><br></center>")
        
        // Notas y firma
        html.append("<a><b>Notas:</b> $notas</a><br>")
        html.append("<a><b>Firma y Cedula:_______________________________</b></a><br>")
        
        // Condiciones para crédito
        if (metodoPago == "2") {
            html.append("<br><br><font size=\"7\"><p>$condicionesCredito</p></font>")
        }
        
        // Pie de página
        html.append("<a>Autorizado mediante oficio <br>N° : 11-1997 de la D.G.T.D </a>")
        
        return html.toString()
    }
    
    override fun updateData() {
        // Obtener actividad y datos de la factura seleccionada
        (activity as? ReimprimirActivity)?.let { actividad ->
            tabSeleccionado = actividad.selecFacturaTab
            
            if (tabSeleccionado == 1) {
                facturaId = actividad.invoiceIdReimprimir
                facturaId?.let { id ->
                    Log.d(TAG, "Factura seleccionada ID: $id")
                    cargarFactura(id)
                }
            } else {
                Log.d(TAG, "No hay factura seleccionada")
            }
        }
    }
    
    /**
     * Carga los datos de una factura desde Realm
     */
    private fun cargarFactura(id: String) {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { r ->
                facturaSeleccionada = r.where(sale::class.java)
                    .equalTo("invoice_id", id)
                    .findFirst()
            }
            
            facturaSeleccionada?.let { factura ->
                Log.d(TAG, "Factura cargada: ${factura.invoice_id}")
                renderizarVistaPrevia()
            }
        } finally {
            realm.close()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        bluetoothStateChangeReceiver?.let { receiver ->
            try {
                activity?.unregisterReceiver(receiver)
            } catch (e: Exception) {
                Log.e(TAG, "Error al desregistrar receptor: ${e.message}")
            }
        }
        bluetoothStateChangeReceiver = null
    }
}


