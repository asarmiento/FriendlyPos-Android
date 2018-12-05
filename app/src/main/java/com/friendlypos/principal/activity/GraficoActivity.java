package com.friendlypos.principal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.crearCliente.modelo.customer_new;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.principal.modelo.datosTotales;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
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

    private static Button btnPorMes;
    private static Button btnPorDia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        chart = (BarChart) findViewById(R.id.BarGrafico);
        toolbar = (Toolbar) findViewById(R.id.toolbarGrafico);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Gráfico por Mes");
        btnPorDia = (Button) findViewById(R.id.btnPorDia);
        btnPorMes = (Button) findViewById(R.id.btnPorMes);

        graficoPorMes();
        btnPorMes.setVisibility(View.INVISIBLE);

       /* btnPorDia.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            toolbar.setTitle("Gráfico por Día");
                            btnPorDia.setVisibility(View.INVISIBLE);
                            btnPorMes.setVisibility(View.VISIBLE);
                            graficoPorDia();

                        }
                        catch (Exception e) {
                            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                });

        btnPorMes.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try {
                            toolbar.setTitle("Gráfico por Mes");
                            graficoPorMes();
                            btnPorMes.setVisibility(View.INVISIBLE);
                            btnPorDia.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Functions.CreateMessage(getApplication(), "Error", e.getMessage() + "\n" + e.getStackTrace().toString());
                        }

                    }
                }

        );*/




    }

    public void graficoPorMes(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm12 = Realm.getDefaultInstance();

        distr = 0.0;
        vent = 0.0;
        prev = 0.0;
        prof = 0.0;
        rec = 0.0;

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

    public void graficoPorDia(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        distr = 0.0;
        vent = 0.0;
        prev = 0.0;
        prof = 0.0;
        rec = 0.0;

        Realm realm19 = Realm.getDefaultInstance();

        RealmQuery<datosTotales> query19 = realm19.where(datosTotales.class).equalTo("date", currentDateandTime);
        final RealmResults<datosTotales> invoice19 = query19.findAll();

        Log.d("qweqweq", invoice19.toString());


        if (invoice19.isEmpty()) {

            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }
        else {

                for (int i = 0; i < invoice19.size(); i++) {

                    List<datosTotales> salesList1 = realm19.where(datosTotales.class).equalTo("date", currentDateandTime).findAll();
                    int id = salesList1.get(i).getId();
                    String date = salesList1.get(i).getDate();


                    if (date.equals(currentDateandTime)) {

                        if (id == 1) {
                            distr = salesList1.get(i).getTotalDistribucion();
                        } else {
                        }

                        if (id == 2) {
                            vent = salesList1.get(i).getTotalVentaDirecta();
                        } else {
                        }
                        if (id == 3) {
                            prev = salesList1.get(i).getTotalPreventa();
                        } else {
                        }
                        if (id == 4) {
                            prof = salesList1.get(i).getTotalProforma();
                        } else {
                        }
                        if (id == 5) {
                            rec = salesList1.get(i).getTotalRecibos();
                        } else {
                        }

                        Log.d("qweqweq1", invoice19 + "");

                    }

                }

                    realm19.close();

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
