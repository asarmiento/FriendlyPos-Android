package com.friendlypos.principal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.crearCliente.modelo.customer_new;
import com.friendlypos.principal.modelo.datosTotales;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class GraficoActivity extends AppCompatActivity {
    Toolbar toolbar;
    private BarChart barGrafico;
    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;
    double distr = 0.0;
    double vent = 0.0;
    double prev = 0.0;
    double prof = 0.0;
    double rec = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        chart = (BarChart) findViewById(R.id.BarGrafico);
        toolbar = (Toolbar) findViewById(R.id.toolbarGrafico);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm12 = Realm.getDefaultInstance();



        RealmQuery<datosTotales> query12 = realm12.where(datosTotales.class);
        final RealmResults<datosTotales> invoice12 = query12.findAll();
        Log.d("qweqweq", invoice12.toString());

        if (invoice12.isEmpty()) {

            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }
        else {

            List<datosTotales> listaNuevo = realm12.copyFromRealm(invoice12);
            distr = listaNuevo.get(0).getTotalDistribucion();
            vent = listaNuevo.get(1).getTotalVentaDirecta();
            prev = listaNuevo.get(2).getTotalPreventa();
            prof = listaNuevo.get(3).getTotalProforma();
            rec = listaNuevo.get(4).getTotalRecibos();

            Log.d("qweqweq1", listaNuevo + "");

            realm12.close();

            BARENTRY = new ArrayList<>();

            BarEntryLabels = new ArrayList<String>();

            AddValuesToBARENTRY();

            AddValuesToBarEntryLabels();

            Bardataset = new BarDataSet(BARENTRY, "Categoria");

            BARDATA = new BarData(BarEntryLabels, Bardataset);
            BARDATA.setValueTextSize(12f);
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

            chart.setData(BARDATA);
            chart.setDescription("");
            chart.animateY(3000);
            chart.setMaxVisibleValueCount(60);
            chart.setPinchZoom(false);

            chart.getAxisRight().setEnabled(false);
            chart.getLegend().setEnabled(false);


            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(true);
            xAxis.setSpaceBetweenLabels(1);

        }
    }


    public void AddValuesToBARENTRY(){

        BARENTRY.add(new BarEntry((float) distr, 0));
        BARENTRY.add(new BarEntry((float) vent, 1));
        BARENTRY.add(new BarEntry((float) prev, 2));
        BARENTRY.add(new BarEntry((float) prof, 3));
        BARENTRY.add(new BarEntry((float) rec, 4));

    }

    public void AddValuesToBarEntryLabels(){

        BarEntryLabels.add("Distr");
        BarEntryLabels.add("Vent Dir");
        BarEntryLabels.add("Prev");
        BarEntryLabels.add("Prof");
        BarEntryLabels.add("Rec");


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(GraficoActivity.this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d("ATRAS", "Atras");
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(GraficoActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
