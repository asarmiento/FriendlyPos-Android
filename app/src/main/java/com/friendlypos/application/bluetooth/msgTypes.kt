package com.friendlysystemgroup.friendlypos.application.bluetooth

/**
 * Created by hgode on 04.04.2014.
 */
object msgTypes {
    // Key names received from the BluetoothChatService Handler
    const val DEVICE_NAME: String = "device_name"
    const val TOAST: String = "toast"
    const val INFO: String = "info"
    const val STATE: String = "state"
    const val READ: String = "read"
    const val WRITE: String = "write"

    // Message types sent from the BluetoothChatService Handler
    const val MESSAGE_STATE_CHANGE: Int = 1
    const val MESSAGE_READ: Int = 2
    const val MESSAGE_WRITE: Int = 3
    const val MESSAGE_DEVICE_NAME: Int = 4
    const val MESSAGE_TOAST: Int = 5
    const val MESSAGE_INFO: Int = 6
}
