package com.friendlypos.principal.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.friendlypos.R
import com.friendlypos.application.util.Functions.CreateMessage
import com.friendlypos.principal.modelo.datosTotales
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.Date

class GraficoActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null
    private val barGrafico: BarChart? = null
    var chart: BarChart? = null
    var BARENTRY: ArrayList<BarEntry>? = null
    var BarEntryLabels: ArrayList<String>? = null
    var Bardataset: BarDataSet? = null
    var BARDATA: BarData? = null
    var distr: Double = 0.0
    var vent: Double = 0.0
    var prev: Double = 0.0
    var prof: Double = 0.0
    var rec: Double = 0.0


    var distr1: Double = 0.0
    var vent1: Double = 0.0
    var prev1: Double = 0.0
    var prof1: Double = 0.0
    var rec1: Double = 0.0

    private var totalizarDistGraf = 0.0
    private var totalizarVentGraf = 0.0
    private var totalizarProfGraf = 0.0
    private var totalizarPrevGraf = 0.0
    private var totalizarRecGraf = 0.0


    fun getTotalizarDistGraf(): Double {
        return totalizarDistGraf
    }

    fun setTotalizarDistGraf(totalizarDistGraf: Double) {
        this.totalizarDistGraf = this.totalizarDistGraf + totalizarDistGraf
    }

    fun getTotalizarVentGraf(): Double {
        return totalizarVentGraf
    }

    fun setTotalizarVentGraf(totalizarVentGraf: Double) {
        this.totalizarVentGraf = this.totalizarVentGraf + totalizarVentGraf
    }

    fun getTotalizarProfGraf(): Double {
        return totalizarProfGraf
    }

    fun setTotalizarProfGraf(totalizarProfGraf: Double) {
        this.totalizarProfGraf = this.totalizarProfGraf + totalizarProfGraf
    }

    fun getTotalizarPrevGraf(): Double {
        return totalizarPrevGraf
    }

    fun setTotalizarPrevGraf(totalizarPrevGraf: Double) {
        this.totalizarPrevGraf = this.totalizarPrevGraf + totalizarPrevGraf
    }

    fun getTotalizarRecGraf(): Double {
        return totalizarRecGraf
    }

    fun setTotalizarRecGraf(totalizarRecGraf: Double) {
        this.totalizarRecGraf = this.totalizarRecGraf + totalizarRecGraf
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico)

        chart = findViewById<View>(R.id.BarGrafico) as BarChart
        toolbar = findViewById<View>(R.id.toolbarGrafico) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar!!.title = "Gráfico por Mes"
        btnPorDia = findViewById<View>(R.id.btnPorDia) as Button
        btnPorMes = findViewById<View>(R.id.btnPorMes) as Button

        graficoPorMes()
        btnPorMes!!.visibility = View.INVISIBLE

        btnPorDia!!.setOnClickListener {
            try {
                toolbar!!.title = "Gráfico por Día"
                btnPorDia!!.visibility = View.INVISIBLE
                btnPorMes!!.visibility = View.VISIBLE
                graficoPorDia()
            } catch (e: Exception) {
                Toast.makeText(application, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        btnPorMes!!.setOnClickListener {
            try {
                toolbar!!.title = "Gráfico por Mes"
                graficoPorMes()
                btnPorMes!!.visibility = View.INVISIBLE
                btnPorDia!!.visibility = View.VISIBLE
            } catch (e: Exception) {
                CreateMessage(
                    application,
                    "Error",
                    """
                        ${e.message}
                        ${e.stackTrace}
                        """.trimIndent()
                )
            }
        }
    }

    fun graficoPorMes() {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime = sdf.format(Date())

        val realm12 = Realm.getDefaultInstance()

        distr = 0.0
        vent = 0.0
        prev = 0.0
        prof = 0.0
        rec = 0.0
        distr1 = 0.0
        vent1 = 0.0
        prev1 = 0.0
        prof1 = 0.0
        rec1 = 0.0
        totalizarDistGraf = 0.0
        totalizarVentGraf = 0.0
        totalizarProfGraf = 0.0
        totalizarPrevGraf = 0.0
        totalizarRecGraf = 0.0

        val invoice12 = realm12.where(datosTotales::class.java).findAll()
        Log.d("qweqweq", invoice12.toString())

        if (invoice12.isEmpty()) {
            Toast.makeText(applicationContext, "Favor descargar datos primero", Toast.LENGTH_LONG)
                .show()
        } else {
            // printSalesCashTotal= 0.0;
            for (i in invoice12.indices) {
                val salesList1: List<datosTotales> = realm12.where(
                    datosTotales::class.java
                ).findAll()
                distr1 = salesList1[i].totalDistribucion
                vent1 = salesList1[i].totalVentaDirecta
                prev1 = salesList1[i].totalPreventa
                prof1 = salesList1[i].totalProforma
                rec1 = salesList1[i].totalRecibos

                if (distr1 != 0.0) {
                    setTotalizarDistGraf(distr1)
                    distr = getTotalizarDistGraf()
                } else if (vent1 != 0.0) {
                    setTotalizarVentGraf(vent1)
                    vent = getTotalizarVentGraf()
                } else if (prev1 != 0.0) {
                    setTotalizarPrevGraf(prev1)
                    prev = getTotalizarPrevGraf()
                } else if (prof1 != 0.0) {
                    setTotalizarProfGraf(prof1)
                    prof = getTotalizarProfGraf()
                } else if (rec1 != 0.0) {
                    setTotalizarRecGraf(rec1)
                    rec = getTotalizarRecGraf()
                }
            }

            realm12.close()

            BARENTRY = ArrayList<BarEntry>()

            BarEntryLabels = ArrayList()

            AddValuesToBARENTRY()

            AddValuesToBarEntryLabels()

            Bardataset = BarDataSet(BARENTRY, "Categoria")

            BARDATA = BarData(BarEntryLabels, Bardataset)
            BARDATA.setValueTextSize(12f)
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS)

            chart.setData(BARDATA)
            chart.setDescription("")
            chart.animateY(3000)
            chart.setMaxVisibleValueCount(60)
            chart.setPinchZoom(false)

            chart.getAxisRight().setEnabled(false)
            chart.getLegend().setEnabled(false)


            val xAxis: XAxis = chart.getXAxis()
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
            //xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(true)
            xAxis.setSpaceBetweenLabels(1)
        }
    }

    fun graficoPorDia() {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime = sdf.format(Date())

        distr = 0.0
        vent = 0.0
        prev = 0.0
        prof = 0.0
        rec = 0.0
        distr1 = 0.0
        vent1 = 0.0
        prev1 = 0.0
        prof1 = 0.0
        rec1 = 0.0
        totalizarDistGraf = 0.0
        totalizarVentGraf = 0.0
        totalizarProfGraf = 0.0
        totalizarPrevGraf = 0.0
        totalizarRecGraf = 0.0

        val realm19 = Realm.getDefaultInstance()

        val invoice12 =
            realm19.where(datosTotales::class.java).equalTo("date", currentDateandTime).findAll()
        Log.d("qweqweq", invoice12.toString())

        if (invoice12.isEmpty()) {
            Toast.makeText(applicationContext, "Favor descargar datos primero", Toast.LENGTH_LONG)
                .show()
        } else {
            // printSalesCashTotal= 0.0;
            for (i in invoice12.indices) {
                val salesList1: List<datosTotales> = realm19.where(
                    datosTotales::class.java
                ).equalTo("date", currentDateandTime).findAll()
                distr1 = salesList1[i].totalDistribucion
                vent1 = salesList1[i].totalVentaDirecta
                prev1 = salesList1[i].totalPreventa
                prof1 = salesList1[i].totalProforma
                rec1 = salesList1[i].totalRecibos

                if (distr1 != 0.0) {
                    setTotalizarDistGraf(distr1)
                    distr = getTotalizarDistGraf()
                } else if (vent1 != 0.0) {
                    setTotalizarVentGraf(vent1)
                    vent = getTotalizarVentGraf()
                } else if (prev1 != 0.0) {
                    setTotalizarPrevGraf(prev1)
                    prev = getTotalizarPrevGraf()
                } else if (prof1 != 0.0) {
                    setTotalizarProfGraf(prof1)
                    prof = getTotalizarProfGraf()
                } else if (rec1 != 0.0) {
                    setTotalizarRecGraf(rec1)
                    rec = getTotalizarRecGraf()
                }
            }

            realm19.close()

            BARENTRY = ArrayList<BarEntry>()

            BarEntryLabels = ArrayList()

            AddValuesToBARENTRY()

            AddValuesToBarEntryLabels()

            Bardataset = BarDataSet(BARENTRY, "Categoria")

            BARDATA = BarData(BarEntryLabels, Bardataset)
            BARDATA.setValueTextSize(12f)
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS)

            chart.setData(BARDATA)
            chart.setDescription("")
            chart.animateY(3000)
            chart.setMaxVisibleValueCount(60)
            chart.setPinchZoom(false)

            chart.getAxisRight().setEnabled(false)
            chart.getLegend().setEnabled(false)


            val xAxis: XAxis = chart.getXAxis()
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
            //xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(true)
            xAxis.setSpaceBetweenLabels(1)
        }
    }


    fun AddValuesToBARENTRY() {
        BARENTRY!!.add(BarEntry(distr.toFloat(), 0))
        BARENTRY!!.add(BarEntry(vent.toFloat(), 1))
        BARENTRY!!.add(BarEntry(prev.toFloat(), 2))
        BARENTRY!!.add(BarEntry(prof.toFloat(), 3))
        BARENTRY!!.add(BarEntry(rec.toFloat(), 4))
    }

    fun AddValuesToBarEntryLabels() {
        BarEntryLabels!!.add("Distr")
        BarEntryLabels!!.add("Vent Dir")
        BarEntryLabels!!.add("Prev")
        BarEntryLabels!!.add("Prof")
        BarEntryLabels!!.add("Rec")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            val intent = Intent(this@GraficoActivity, MenuPrincipal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Log.d("ATRAS", "Atras")
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@GraficoActivity, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private var btnPorMes: Button? = null
        private var btnPorDia: Button? = null
    }
}
