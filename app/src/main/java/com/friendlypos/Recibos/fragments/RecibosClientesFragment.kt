package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.adapters.RecibosClientesAdapter
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import io.realm.Realm
import io.realm.RealmResults
import io.realm.internal.SyncObjectServerFacade

class RecibosClientesFragment : BaseFragment() {
    private var realm: Realm? = null

    @BindView(R.id.recyclerViewRecibosCliente)
    lateinit var recyclerView: RecyclerView

    private var adapter: RecibosClientesAdapter? = null


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_recibos_clientes, container,
            false
        )
        //ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (adapter == null) {
            adapter = RecibosClientesAdapter(
                context!!, ((activity as RecibosActivity?)!!),  /* removeDuplicates(*/
                listClientes /*)*/
            )
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private val listClientes: List<recibos>
        get() {
            realm = Realm.getDefaultInstance()
            val query = realm.where(recibos::class.java)

            val result1: RealmResults<recibos> = query.distinct("customer_id")

            if (result1.size == 0) {
                Toast.makeText(
                    SyncObjectServerFacade.getApplicationContext(),
                    "Favor descargar datos primero",
                    Toast.LENGTH_LONG
                ).show()
            }

            Log.d("Resultado", result1.toString())
            return result1
        }


    /*
   public ArrayList<recibos> removeDuplicates(List<recibos> list){
       Set set = new TreeSet(new Comparator() {

           @Override
           public int compare(Object o1, Object o2) {
               if(((recibos)o1).getCustomer_id().equalsIgnoreCase(((recibos)o2).getCustomer_id())){
                   return 0;
               }
               return 1;
           }
       });
       set.addAll(list);

       final ArrayList newList = new ArrayList(set);
       Log.d("dupli", newList + "");
       return newList;

   }*/
    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }


    override fun updateData() {
    }

    companion object {
        val instance: RecibosClientesFragment
            get() = RecibosClientesFragment()
    }
}
