package com.friendlysystemgroup.friendlypos.application.util

import android.content.Context
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale

/**
 * Clase adaptadora que mantiene compatibilidad con el código antiguo mientras
 * utiliza la nueva implementación de PrintingManager
 * 
 * Esta clase implementa un patrón adaptador para permitir una transición gradual
 * hacia la nueva arquitectura de impresión sin romper el código existente.
 */
object PrinterAdapter {
    
    // Referencia al administrador de impresión
    private val printingManager = PrintingManager.getInstance()
    
    /**
     * Imprime una factura de distribución (mantiene compatibilidad con PrinterFunctions)
     */
    fun imprimirFacturaDistrTotal(
        sale: sale,
        context: Context,
        tipoImpresion: Int,
        cantidadImpresiones: String
    ) {
        printingManager.imprimirFactura(sale, context, tipoImpresion, cantidadImpresiones)
    }
    
    /**
     * Imprime una factura de venta directa (mantiene compatibilidad con PrinterFunctions)
     */
    fun imprimirFacturaVentaDirectaTotal(
        sale: sale,
        context: Context,
        tipoImpresion: Int,
        cantidadImpresiones: String
    ) {
        printingManager.imprimirFactura(sale, context, tipoImpresion, cantidadImpresiones)
    }
    
    /**
     * Imprime un recibo (mantiene compatibilidad con PrinterFunctions)
     */
    fun imprimirRecibo(
        recibo: receipts,
        context: Context,
        cantidadImpresiones: String
    ) {
        printingManager.imprimirRecibo(recibo, context, cantidadImpresiones)
    }
    
    /**
     * Imprime una liquidación desde el menú principal
     */
    fun imprimirLiquidacionMenu(context: Context) {
        printingManager.imprimirLiquidacion(context)
    }
    
    /**
     * Imprime una devolución desde el menú principal
     */
    fun imprimirDevoluciónMenu(context: Context) {
        printingManager.imprimirDevolucion(context)
    }
    
    /**
     * Imprime una orden de carga desde el menú principal
     */
    fun imprimirOrdenCarga(context: Context) {
        printingManager.imprimirOrdenCarga(context)
    }
} 