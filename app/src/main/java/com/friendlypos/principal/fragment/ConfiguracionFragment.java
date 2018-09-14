package com.friendlypos.principal.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.principal.activity.MenuPrincipal;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class ConfiguracionFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, /*OnInitListener, */Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private String noDevicesPaired = "No hay dispositivos";

    private String impZebra = "Impresora Zebra";
    private String otraImp = "Otra Impresora";
    private ListPreference listPref;
    private BluetoothDevice currentDevice;
    private Preference prefDesvincularImpresora, prefConectarBluetooth, /*prefConectarRed, */prefSelecImpresora;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        // mTts = new TextToSpeech(getActivity(), this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(view !=null){
            view.setBackgroundColor(getResources().getColor(R.color.background));
            view.setAlpha((float) 0.8);
        }

        populateExternalDisplayDevices();
        populateExternalSeleccImpresora();

        prefSelecImpresora = findPreference("pref_selec_impresora");
        prefSelecImpresora.setOnPreferenceClickListener(this);

        prefConectarBluetooth = findPreference("pref_conectar_bluetooth");
        prefConectarBluetooth.setOnPreferenceClickListener(this);

        prefDesvincularImpresora = findPreference("pref_desvincular_impresora");
        prefDesvincularImpresora.setOnPreferenceClickListener(this);

        // Set values
        setBTDeviceSummary("pref_conectar_bluetooth");

       /* prefConectarRed = findPreference("pref_conectar_red");
        prefConectarRed.setOnPreferenceClickListener(this);*/
        return view;

    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Intent i;

        // Update value of any list preference
        Preference pref = findPreference(key);
        // Callbacks to input dr

    }

    private void desvincularImpresora(){
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle("Desvincular Impresora");
        build.setMessage("¿Desea desvincular la impresora?");
        build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                linkOrUnlinkDevice(noDevicesPaired,"U");
            }
        });
        build.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = build.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setBTDeviceSummary(String key){
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String value = sharedPref.getString(key,noDevicesPaired);
            pref.setSummary(getNameDevice(value));
        }
    }

    private String getNameDevice(String value){
        String name = "";

        if(!value.equals(noDevicesPaired)) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled()) {
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getAddress().equals(value)) {
                                name = device.getName();
                                currentDevice = device;
                            }
                        }
                    }
                }
            }
        }else{
            name = value;
        }
        return name;
    }

    private void populateExternalSeleccImpresora(){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();

        listPref = (ListPreference) findPreference("pref_selec_impresora");
        listPref.setDefaultValue("1");

        //linkOrUnlinkDevice(noDevicesPaired,"");
    }


    private void populateExternalDisplayDevices(){
        CharSequence[] labels = {noDevicesPaired};
        CharSequence[] values = {noDevicesPaired};

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    List<String> list_values = new ArrayList<>();
                    List<String> list_labels = new ArrayList<>();

                    for (BluetoothDevice device : pairedDevices) {
                        list_labels.add(device.getName());
                        list_values.add(device.getAddress());
                    }

                    list_labels.add(noDevicesPaired);
                    list_values.add(noDevicesPaired);

                    labels = list_labels.toArray(new CharSequence[list_labels.size()]);
                    values = list_values.toArray(new CharSequence[list_values.size()]);
                }
            }
        }

        listPref = (ListPreference) findPreference("pref_conectar_bluetooth");
        listPref.setEntries(labels);
        listPref.setEntryValues(values);
        listPref.setOnPreferenceChangeListener(this);
        //linkOrUnlinkDevice(noDevicesPaired,"");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //mTts.shutdown();
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("ListPreference","OnPreferenceChange");
        linkOrUnlinkDevice(newValue.toString(),"");
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if( preference == prefDesvincularImpresora) {
            desvincularImpresora();
        }else if (preference == prefConectarBluetooth){
            populateExternalDisplayDevices();
        }
       /* else if (preference == prefConectarRed){

            ImprimirRedImpresora();
        }*/

        return true;
    }

    private void linkOrUnlinkDevice(String value,String operation){
        Preference preference = findPreference("pref_conectar_bluetooth");
        preference.getEditor().putString("pref_conectar_bluetooth", value).commit();
        preference.setSummary(getNameDevice(value));
        listPref.setValue(value);

        if(value == "No hay dispositivos"){
            Functions.CreateMessage(getActivity(), "Error", "No hay ninguna conexión activa");

        }
        else{
            if(operation.equals("U")) {
                try {
                    Method m = currentDevice.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(currentDevice, (Object[]) null);
                    preference.getEditor().remove("pref_conectar_bluetooth").commit();
                    populateExternalDisplayDevices();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }}

        populateExternalDisplayDevices();
    }

    private void ImprimirRedImpresora(){
        CharSequence[] labels = {noDevicesPaired};
        CharSequence[] values = {noDevicesPaired};

        listPref = (ListPreference) findPreference("pref_conectar_red");
        listPref.setEntries(null);
        listPref.setEntryValues(null);
        listPref.setOnPreferenceChangeListener(this);

        Toast.makeText(getActivity(), "Botón no disponible por el momento", Toast.LENGTH_SHORT).show();
    }

}
