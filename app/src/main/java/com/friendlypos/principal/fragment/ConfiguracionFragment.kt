package com.friendlypos.principal.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlypos.R
import com.friendlypos.application.util.Functions.CreateMessage
import java.lang.reflect.InvocationTargetException

class ConfiguracionFragment : PreferenceFragment(), OnSharedPreferenceChangeListener,
    OnPreferenceChangeListener, OnPreferenceClickListener {
    private val noDevicesPaired = "No hay dispositivos"

    private val impZebra = "Impresora Zebra"
    private val otraImp = "Otra Impresora"
    private var listPref: ListPreference? = null
    private var currentDevice: BluetoothDevice? = null
    private var prefDesvincularImpresora: Preference? = null
    private var prefConectarBluetooth: Preference? = null /*prefConectarRed, */
    private var prefSelecImpresora: Preference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)
        // mTts = new TextToSpeech(getActivity(), this);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            view.setBackgroundColor(resources.getColor(R.color.icons))
            view.alpha = 0.8.toFloat()
        }

        populateExternalDisplayDevices()
        populateExternalSeleccImpresora()

        prefSelecImpresora = findPreference("pref_selec_impresora")
        prefSelecImpresora.setOnPreferenceClickListener(this)

        prefConectarBluetooth = findPreference("pref_conectar_bluetooth")
        prefConectarBluetooth.setOnPreferenceClickListener(this)

        prefDesvincularImpresora = findPreference("pref_desvincular_impresora")
        prefDesvincularImpresora.setOnPreferenceClickListener(this)

        // Set values
        setBTDeviceSummary("pref_conectar_bluetooth")

        /* prefConectarRed = findPreference("pref_conectar_red");
        prefConectarRed.setOnPreferenceClickListener(this);*/
        return view
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        var i: Intent

        // Update value of any list preference
        val pref = findPreference(key)

        // Callbacks to input dr
    }

    private fun desvincularImpresora() {
        val build = AlertDialog.Builder(
            activity
        )
        build.setTitle("Desvincular Impresora")
        build.setMessage("¿Desea desvincular la impresora?")
        build.setPositiveButton(
            "OK"
        ) { dialog, which ->
            linkOrUnlinkDevice(
                noDevicesPaired,
                "U"
            )
        }
        build.setNegativeButton(
            "CANCELAR"
        ) { dialog, which -> }

        val dialog = build.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setBTDeviceSummary(key: String) {
        val pref = findPreference(key)
        if (pref is ListPreference) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(
                activity
            )
            val value = sharedPref.getString(key, noDevicesPaired)
            pref.setSummary(getNameDevice(value!!))
        }
    }

    private fun getNameDevice(value: String): String {
        var name = ""

        if (value != noDevicesPaired) {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled) {
                    val pairedDevices = bluetoothAdapter.bondedDevices
                    if (pairedDevices.size > 0) {
                        for (device in pairedDevices) {
                            if (device.address == value) {
                                name = device.name
                                currentDevice = device
                            }
                        }
                    }
                }
            }
        } else {
            name = value
        }
        return name
    }

    private fun populateExternalSeleccImpresora() {
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.commit()

        listPref = findPreference("pref_selec_impresora") as ListPreference
        listPref!!.setDefaultValue("1")

        //linkOrUnlinkDevice(noDevicesPaired,"");
    }


    private fun populateExternalDisplayDevices() {
        var labels = arrayOf<CharSequence>(noDevicesPaired)
        var values = arrayOf<CharSequence>(noDevicesPaired)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled) {
                val pairedDevices = bluetoothAdapter.bondedDevices
                if (pairedDevices.size > 0) {
                    val list_values: MutableList<String> = ArrayList()
                    val list_labels: MutableList<String> = ArrayList()

                    for (device in pairedDevices) {
                        list_labels.add(device.name)
                        list_values.add(device.address)
                    }

                    list_labels.add(noDevicesPaired)
                    list_values.add(noDevicesPaired)

                    labels = list_labels.toTypedArray<CharSequence>()
                    values = list_values.toTypedArray<CharSequence>()
                }
            }
        }

        listPref = findPreference("pref_conectar_bluetooth") as ListPreference
        listPref!!.entries = labels
        listPref!!.entryValues = values
        listPref!!.onPreferenceChangeListener = this
        //linkOrUnlinkDevice(noDevicesPaired,"");
    }

    override fun onDestroy() {
        super.onDestroy()
        //mTts.shutdown();
    }


    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        Log.d("ListPreference", "OnPreferenceChange")
        linkOrUnlinkDevice(newValue.toString(), "")
        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (preference === prefDesvincularImpresora) {
            desvincularImpresora()
        } else if (preference === prefConectarBluetooth) {
            populateExternalDisplayDevices()
        }

        /* else if (preference == prefConectarRed){

            ImprimirRedImpresora();
        }*/
        return true
    }

    private fun linkOrUnlinkDevice(value: String, operation: String) {
        val preference = findPreference("pref_conectar_bluetooth")
        preference.editor.putString("pref_conectar_bluetooth", value).commit()
        preference.summary = getNameDevice(value)
        listPref!!.value = value

        if (value === "No hay dispositivos") {
            CreateMessage(activity, "Error", "No hay ninguna conexión activa")
        } else {
            if (operation == "U") {
                try {
                    val m = currentDevice!!.javaClass.getMethod(
                        "removeBond",
                        *null as Array<Class<*>?>?
                    )
                    m.invoke(currentDevice, *null as Array<Any?>?)
                    preference.editor.remove("pref_conectar_bluetooth").commit()
                    populateExternalDisplayDevices()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }

        populateExternalDisplayDevices()
    }

    private fun ImprimirRedImpresora() {
        val labels = arrayOf<CharSequence>(noDevicesPaired)
        val values = arrayOf<CharSequence>(noDevicesPaired)

        listPref = findPreference("pref_conectar_red") as ListPreference
        listPref.setEntries(null)
        listPref.setEntryValues(null)
        listPref!!.onPreferenceChangeListener = this

        Toast.makeText(activity, "Botón no disponible por el momento", Toast.LENGTH_SHORT).show()
    }
}
