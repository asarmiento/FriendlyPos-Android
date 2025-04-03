package com.friendlypos.reenvio_email.adapters

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.datamanager.BaseManager.Companion.api
import com.friendlypos.application.interfaces.RequestInterface
import com.friendlypos.application.util.Functions.CreateMessage
import com.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.reenvio_email.activity.EmailActivity
import com.friendlypos.reenvio_email.modelo.EmailResponse
import com.friendlypos.reenvio_email.modelo.customer
import com.friendlypos.reenvio_email.modelo.email_Id
import com.friendlypos.reenvio_email.modelo.invoices
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class EmailClientesAdapter(
    context: Context?,
    private val activity: EmailActivity,
    var contentList: MutableList<Clientes>
) :
    RecyclerView.Adapter<EmailClientesAdapter.CharacterViewHolder>() {
    var activa: Int = 0
    private var selected_position = -1
    var idCliente: String? = null
    private val mAPIService: RequestInterface?
    var token: String? = null
    private val networkStateChangeReceiver: NetworkStateChangeReceiver
    var codigo: Int = 0
    var respuestaServer: String? = null
    var codigoS: customer? = null
    var mensajeS: List<invoices>? = null
    var resultS: String? = null
    var codigoServer: Int = 0
    var mContentsArray: ArrayList<invoices>
    var mContentsArray2: ArrayList<customer>
    private var realm: Realm? = null
    var tabCliente: Int = 0

    init {
        QuickContext = context
        networkStateChangeReceiver = NetworkStateChangeReceiver()
        mAPIService = api
        mContentsArray = ArrayList()
        mContentsArray2 = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_clientes_email, parent, false)

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val content = contentList[position]


        holder.txt_cliente_card.text = content.card
        holder.txt_cliente_fantasyname.text = content.fantasyName
        holder.txt_cliente_companyname.text = content.companyName
        holder.txt_cliente_address.text = content.address
        holder.txt_cliente_telefono.text = content.phone

        holder.cardView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                realm = Realm.getDefaultInstance()
                realm.executeTransaction(Realm.Transaction { realm ->
                    val result = realm.where(invoices::class.java).findAll()
                    result.deleteAllFromRealm()
                })

                token = "Bearer " + get(QuickContext!!).token
                Log.d("tokenC", "$token ")

                activa = 1


                /*final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                progresRing.setCancelable(true);*/
                val progresRing =
                    ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
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
                    //  progresRing.dismiss();
                }.start()

                val pos = position
                if (pos == RecyclerView.NO_POSITION) return

                // Updating old as well as new positions
                notifyItemChanged(selected_position)
                selected_position = position
                notifyItemChanged(selected_position)

                idCliente = content.id

                val id = email_Id(idCliente)
                val adswd = id.costumer
                Log.d("EnviarEmaiasdasdl", adswd + "")



                if (this.isOnline) {
                    Log.d("factura1", "$idCliente ")
                    tabCliente = 1
                    activity.selecClienteTabEmail = tabCliente
                    val obj = email_Id(idCliente)
                    Log.d("obj", "$obj ")
                    mAPIService!!.savePostEmail(obj, token)
                        .enqueue(object : retrofit2.Callback<EmailResponse?> {
                            override fun onResponse(
                                call: retrofit2.Call<EmailResponse?>?,
                                response: retrofit2.Response<EmailResponse?>
                            ) {
                                mContentsArray.clear()


                                if (response.isSuccessful()) {
                                    mContentsArray.addAll(response.body().getFacturas())

                                    try {
                                        // Work with Realm


                                        realm.beginTransaction()
                                        realm.copyToRealmOrUpdate(mContentsArray)
                                        realm.commitTransaction()
                                        //realm.close();
                                    } finally {
                                        realm.close()
                                    }
                                    Log.d("GuardarFacturas", mContentsArray.toString())

                                    /*  Log.d("respuestaFactura",response.body().toString());
                                codigo = response.code();
                                codigoS = response.body().getCustomer();
                                mensajeS = response.body().getFacturas();*/
                                    //  resultS= String.valueOf(response.body().isResult());
                                } else {
                                    /*     RealmResults<invoices> results = realm.where(invoices.class).findAll();
                                if (results.isEmpty()){
                                }else{
                                    mContentsArray.addAll(results);
                                }*/
                                }
                                progresRing.dismiss()
                            }


                            override fun onFailure(
                                call: retrofit2.Call<EmailResponse?>?,
                                t: Throwable?
                            ) {
                                progresRing.dismiss()
                                Log.e(ContentValues.TAG, "Unable to submit post to API.")
                            }
                        })
                } else {
                    tabCliente = 0
                    activity.selecClienteTabEmail = tabCliente
                    CreateMessage(
                        activity,
                        "Email",
                        "Por favor revisar conexión de Internet antes de continuar"
                    )
                    progresRing.dismiss()
                }


                /*
                 email_Id obj = new email_Id(idCliente);
                    Log.d("obj", obj + " ");

                Call<EmailResponse> call = mAPIService.savePostEmail(obj, token);

                call.enqueue(new Callback<EmailResponse>() {

                    @Override
                    public void onResponse(Call<EmailResponse> call, Response<EmailResponse> response) {
                        progresRing.dismiss();
                        // Procesar errores
                        if (!response.isSuccessful()) {
                            String error = "Ha ocurrido un error. Contacte al administrador";
                            if (response.errorBody()
                                    .contentType()
                                    .subtype()
                                    .equals("json")) {
                                UserError userError = UserError.fromResponseBody(response.errorBody());

                                error = userError.getMessage();
                                Log.d("LoginActivity", userError.getMessage());
                            }
                            else {
                                try {
                                    // Reportar causas de error no relacionado con la API
                                    Log.d("LoginActivity", response.errorBody().string());
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            showLoginError(error);}else {

                            Log.d("EnviarEmail", response.body() + "");
                            Log.d("EnviarEmail", response.message() + "");
                            Log.d("EnviarEmail", response.code() + "");
                        }
                        session.guardarDatosUsuarioas(userId, password);
                        download1.descargarUsuarios(context);
                        entrarMenuPrincipal();

                        //showAppointmentsScreen();
                    }

                    @Override
                    public void onFailure(Call<EmailResponse> call, Throwable t) {
                        progresRing.dismiss();
                        showLoginError(t.getMessage());
                    }
                });
*/
            }
        })
        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun showLoginError(error: String) {
        Toast.makeText(QuickContext, error, Toast.LENGTH_LONG).show()
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    fun setFilter(countryModels: List<Clientes>) {
        contentList = ArrayList()
        contentList.addAll(countryModels)
        notifyDataSetChanged()
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(QuickContext!!)

    class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewClientes) as CardView
        val txt_cliente_card: TextView =
            view.findViewById<View>(R.id.txt_cliente_card) as TextView
        val txt_cliente_fantasyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_fantasyname) as TextView
        val txt_cliente_companyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_companyname) as TextView
        val txt_cliente_address: TextView =
            view.findViewById<View>(R.id.txt_cliente_address) as TextView
        private val txt_cliente_creditlimit =
            view.findViewById<View>(R.id.txt_cliente_creditlimit) as TextView
        private val txt_cliente_fixeddescount =
            view.findViewById<View>(R.id.txt_cliente_fixeddescount) as TextView
        private val txt_cliente_due =
            view.findViewById<View>(R.id.txt_cliente_due) as TextView
        private val txt_cliente_credittime =
            view.findViewById<View>(R.id.txt_cliente_credittime) as TextView
        val txt_cliente_telefono: TextView =
            view.findViewById<View>(R.id.txt_cliente_telefono) as TextView
        private val btnUbicacionCliente =
            view.findViewById<View>(R.id.btnUbicacionCliente) as ImageButton
        private val btnEditarCliente =
            view.findViewById<View>(R.id.btnEditarCliente) as ImageButton
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var QuickContext: Context? = null
    }
}

