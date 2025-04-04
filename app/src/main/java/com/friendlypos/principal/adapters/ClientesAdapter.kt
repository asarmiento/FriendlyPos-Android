package com.friendlysystemgroup.friendlypos.principal.adapters

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.util.GPSTracker
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.principal.modelo.customer_location
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class ClientesAdapter(context: Context?, var contentList: MutableList<Clientes>) :
    RecyclerView.Adapter<ClientesAdapter.CharacterViewHolder>() {
    var activa: Int = 0
    private var selected_position = -1
    var longitud: Double = 0.0
    var latitud: Double = 0.0
    var gps: GPSTracker? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var nextId: Int = 0
    var idCliente: String? = null
    var obtenida: Int = 0

    init {
        QuickContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_clientes, parent, false)

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val content = contentList[position]

        creditolimite = content.creditLimit.toDouble()
        descuentoFixed = content.fixedDiscount.toDouble()
        cleintedue = content.due.toDouble()
        credittime = content.creditTime.toDouble()

        holder.txt_cliente_card.text = content.card
        holder.txt_cliente_fantasyname.text = content.fantasyName
        holder.txt_cliente_companyname.text = content.companyName
        holder.txt_cliente_address.text = content.address
        holder.txt_cliente_telefono.text = content.phone
        holder.txt_cliente_creditlimit.text =
            String.format("%,.2f", (creditolimite))
        holder.txt_cliente_fixeddescount.text =
            String.format("%,.2f", (descuentoFixed))
        holder.txt_cliente_due.text =
            String.format("%,.2f", (cleintedue))
        holder.txt_cliente_credittime.text =
            String.format("%,.2f", (credittime))

        holder.cardView.setOnClickListener(View.OnClickListener {
            activa = 1
            /*  final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                               progresRing.setCancelable(true);*/
            val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                            "Seleccionando Cliente", true);*/
            val message = "Seleccionando Cliente"
            val titulo = "Cargando"
            val spannableString = SpannableString(message)
            val spannableStringTitulo = SpannableString(titulo)

            val typefaceSpan = CalligraphyTypefaceSpan(
                TypefaceUtils.load(
                    QuickContext!!.assets, "font/monse.otf"
                )
            )
            spannableString.setSpan(
                typefaceSpan,
                0,
                message.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length, Spanned.SPAN_PRIORITY)

            progresRing.setTitle(spannableStringTitulo)
            progresRing.setMessage(spannableString)
            progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progresRing.isIndeterminate = true
            progresRing.setCancelable(true)
            progresRing.show()
            Thread {
                try {
                    Thread.sleep(5000)
                } catch (e: Exception) {
                }
                progresRing.dismiss()
            }.start()

            val pos = position
            if (pos == RecyclerView.NO_POSITION) return@OnClickListener

            // Updating old as well as new positions
            notifyItemChanged(selected_position)
            selected_position = position
            notifyItemChanged(selected_position)

            idCliente = content.id

            //   Toast.makeText(QuickContext, "idCliente "+ idCliente, Toast.LENGTH_LONG).show();
            longitud = content.longitud
            latitud = content.latitud
        })
        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }


        holder.btnUbicacionCliente.setOnClickListener {
            if (activa == 1) {
                if (longitud != 0.0 && latitud != 0.0) {
                    try {
                        val url =
                            "https://waze.com/ul?ll=$latitud,$longitud&navigate=yes"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        QuickContext!!.startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        val gmmIntentUri = Uri.parse("geo:$latitud,$longitud")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        QuickContext!!.startActivity(mapIntent)
                    }
                } else {
                    Toast.makeText(
                        QuickContext,
                        "El cliente no cuenta con dirección GPS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    QuickContext,
                    "Selecciona un cliente primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.btnEditarCliente.setOnClickListener {
            if (activa == 1) {
                editarCliente()
            } else {
                Toast.makeText(
                    QuickContext,
                    "Selecciona una cliente primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    fun setFilter(countryModels: List<Clientes>) {
        contentList = ArrayList()
        contentList.addAll(countryModels)
        notifyDataSetChanged()
    }


    class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewClientes) as CardView
        internal val txt_cliente_card: TextView =
            view.findViewById<View>(R.id.txt_cliente_card) as TextView
        val txt_cliente_fantasyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_fantasyname) as TextView
        val txt_cliente_companyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_companyname) as TextView
        val txt_cliente_address: TextView =
            view.findViewById<View>(R.id.txt_cliente_address) as TextView
        val txt_cliente_creditlimit: TextView =
            view.findViewById<View>(R.id.txt_cliente_creditlimit) as TextView
        val txt_cliente_fixeddescount: TextView =
            view.findViewById<View>(R.id.txt_cliente_fixeddescount) as TextView
        val txt_cliente_due: TextView =
            view.findViewById<View>(R.id.txt_cliente_due) as TextView
        val txt_cliente_credittime: TextView =
            view.findViewById<View>(R.id.txt_cliente_credittime) as TextView
        val txt_cliente_telefono: TextView =
            view.findViewById<View>(R.id.txt_cliente_telefono) as TextView
        val btnUbicacionCliente: ImageButton =
            view.findViewById<View>(R.id.btnUbicacionCliente) as ImageButton
        val btnEditarCliente: ImageButton =
            view.findViewById<View>(R.id.btnEditarCliente) as ImageButton
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }


    fun editarCliente() {
        val layoutInflater = LayoutInflater.from(QuickContext)
        val promptView = layoutInflater.inflate(R.layout.prompt_editar_clientes, null)

        val alertDialogBuilder = AlertDialog.Builder(
            QuickContext!!
        )
        alertDialogBuilder.setView(promptView)


        val label = promptView.findViewById<View>(R.id.promtClabel) as TextView
        val btnObtenerGPS = promptView.findViewById<View>(R.id.btnObtenerGPS) as Button
        val txtEditarLongitud = promptView.findViewById<View>(R.id.txtEditarLongitud) as TextView
        val txtEditarLatitud = promptView.findViewById<View>(R.id.txtEditarLatitud) as TextView

        btnObtenerGPS.setOnClickListener {
            obtenida = 1
            obtenerLocalizacion()
            txtEditarLongitud.text = "Longitud: $longitude"
            txtEditarLatitud.text = "Latitud: $latitude"
        }

        label.text = "Obtenga la nueva ubicación GPS"

        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { dialog, id ->
            if (obtenida == 1) {
                try {
                    val dialogReturnSale = AlertDialog.Builder(
                        QuickContext!!
                    )
                        .setTitle("Guardar")
                        .setMessage("¿Desea cambiar la ubicación del cliente?")
                        .setPositiveButton(
                            "OK"
                        ) { dialog, which ->
                            val realm5 = Realm.getDefaultInstance()
                            realm5.beginTransaction()

                            /* Number currentIdNum = realm5.where(customer_location.class).max("id_location");
                        
                                                                if (currentIdNum == null) {
                                                                    nextId = 1;
                                                                } else {
                                                                    nextId = currentIdNum.intValue() + 1;
                                                                }
                        */
                            val ubicacion = customer_location()

                            //  ubicacion.setId_location(nextId);
                            ubicacion.latitud = latitude
                            ubicacion.longitud = longitude
                            ubicacion.id = idCliente
                            ubicacion.subidaEdit = 1

                            realm5.copyToRealmOrUpdate(ubicacion)
                            realm5.commitTransaction()
                            Log.d("UbicacionNueva", ubicacion.toString() + "")
                            realm5.close()
                        }.setNegativeButton(
                            "Cancel"
                        ) { dialog, which -> dialog.cancel() }.create()
                    dialogReturnSale.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    /* Functions.createSnackBar(QuickContext, coordinatorLayout, "Sucedio un error Revise que el producto y sus dependientes tengan existencias", 2, Snackbar.LENGTH_LONG);
                            Functions.CreateMessage(QuickContext, "Error", e.getMessage());*/
                }
            } else {
                Toast.makeText(
                    QuickContext,
                    "Obtenga la nueva dirección primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }

        val alertD = alertDialogBuilder.create()
        alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        alertD.show()
    }

    fun obtenerLocalizacion() {
        gps = GPSTracker(QuickContext!!)
        if (gps!!.canGetLocation()) {
            latitude = gps!!.getLatitude()
            longitude = gps!!.getLongitude()
        } else {
            gps!!.showSettingsAlert()
        }
    }

    companion object {
        private var creditolimite = 0.0
        private var descuentoFixed = 0.0
        private var cleintedue = 0.0
        private var credittime = 0.0
        private var QuickContext: Context? = null
    }
}
