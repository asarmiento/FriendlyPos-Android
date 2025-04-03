package com.friendlypos.application.bluetooth

/**
 * Created by Desarrollo on 3/30/2015.
 */
object PrinterCommands {
    val INIT: ByteArray = byteArrayOf(27, 64)
    var FEED_LINE: ByteArray = byteArrayOf(10)

    var SELECT_FONT_A: ByteArray = byteArrayOf(27, 33, 1)

    var SET_BAR_CODE_HEIGHT: ByteArray = byteArrayOf(29, 104, 100)
    var PRINT_BAR_CODE_1: ByteArray = byteArrayOf(29, 107, 2)
    var SEND_NULL_BYTE: ByteArray = byteArrayOf(0x00)

    var SELECT_PRINT_SHEET: ByteArray = byteArrayOf(0x1B, 0x63, 0x30, 0x02)
    var FEED_PAPER_AND_CUT: ByteArray = byteArrayOf(0x1D, 0x56, 66, 0x00)

    var SELECT_CYRILLIC_CHARACTER_CODE_TABLE: ByteArray = byteArrayOf(0x1B, 0x74, 0x11)

    var SELECT_BIT_IMAGE_MODE: ByteArray = byteArrayOf(0x1B, 0x2A, 33, -128, 0)
    var SET_LINE_SPACING_24: ByteArray = byteArrayOf(0x1B, 0x33, 24)
    var SET_LINE_SPACING_30: ByteArray = byteArrayOf(0x1B, 0x33, 30)

    var TRANSMIT_DLE_PRINTER_STATUS: ByteArray = byteArrayOf(0x10, 0x04, 0x01)
    var TRANSMIT_DLE_OFFLINE_PRINTER_STATUS: ByteArray = byteArrayOf(0x10, 0x04, 0x02)
    var TRANSMIT_DLE_ERROR_STATUS: ByteArray = byteArrayOf(0x10, 0x04, 0x03)
    var TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS: ByteArray = byteArrayOf(0x10, 0x04, 0x04)
}