package com.friendlypos.principal.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.friendlypos.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraficoActivity extends AppCompatActivity {

    private BarChart barGrafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

    barGrafico = (BarChart) findViewById(R.id.BarGrafico);
        barGrafico.getDescription().setEnabled(false);

        setData(10);
        barGrafico.setFitBars(true);




    }

    private void setData(int count){

        ArrayList<BarEntry> valores = new ArrayList<>();

        for (int i =0; i< count; i++){
            float value = (float) (Math.random()*100);
            valores.add(new BarEntry(i,(int) value));

        }
        BarDataSet set = new BarDataSet(valores, "Data Set");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        BarData data = new BarData(set);

        barGrafico.setData(data);
        barGrafico.invalidate();
        barGrafico.animateY(500);
    }

}
