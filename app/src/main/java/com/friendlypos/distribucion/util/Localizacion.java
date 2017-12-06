package com.friendlypos.distribucion.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.distribucion.fragment.DistTotalizarFragment;

/**
 * Created by DelvoM on 06/12/2017.
 */

public class Localizacion  implements LocationListener {
    DistTotalizarFragment mainActivity;
Context context;
    public DistTotalizarFragment getMainActivity() {

        return mainActivity;
    }

    public void setMainActivity(DistTotalizarFragment mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location loc) {
        // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
        // debido a la deteccion de un cambio de ubicacion
        loc.getLatitude();
        loc.getLongitude();
        String Text = "Mi ubicacion actual es: " + "\n Lat = "
                + loc.getLatitude() + "\n Long = " + loc.getLongitude();
      //  messageTextView.setText(Text);
        Log.d("adda", Text);
        this.mainActivity.setLocation(loc);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Este metodo se ejecuta cuando el GPS es desactivado
        Toast.makeText(context,"GPS Desactivado", Toast.LENGTH_LONG).show();
     //   messageTextView.setText("GPS Desactivado");
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Este metodo se ejecuta cuando el GPS es activado
        Toast.makeText(context,"GPS Activado", Toast.LENGTH_LONG).show();
        //messageTextView.setText("GPS Activado");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Este metodo se ejecuta cada vez que se detecta un cambio en el
        // status del proveedor de localizacion (GPS)
        // Los diferentes Status son:
        // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
        // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
        // espera que este disponible en breve
        // AVAILABLE -> Disponible
    }

}/* End of Class MyLocationListener */
