package com.friendlypos.distribucion.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrResumenAdapter extends RecyclerView.Adapter<DistrResumenAdapter.CharacterViewHolder> {
    private Context context;
    private List<Pivot> productosList;
    private DistribucionActivity activity;
    private ArrayList<Pivot> data;
    private static ArrayList<Pivot> aListdata = new ArrayList<Pivot>();
    private int selected_position1 = -1;
    Double amount_dist_inventario = 0.0;
    int idInvetarioSelec;

    public ArrayList<Pivot> getData() {
        return data;
    }

    private static double iva = 13.0;
    private static int apply_done = 0;
    //IVA
    private static Double subExen = 0.0;
    private static Double subGrab = 0.0;
    private static Double subGrabm = 0.0;
    private static Double IvaT = 0.0;
    private static Double subt = 0.0;
    private static Double discountBill = 0.0;
    private static Double total = 0.0;

    private static String description;
    private static String tipo;
    private static String impuestoIVA;
    private static Double cantidad = 0.0;
    private static Double precio = 0.0;
    private static Double descuento = 0.0;
    private static Double clienteFixedDescuento = 0.0;
    private DistResumenFragment fragment;

    private static String subTotalGrabado, subTotalGrabadoM, subTotalExento, descuentoCliente, subTotal, Total;

    private static Context QuickContext = null;

    public DistrResumenAdapter(Context context, DistribucionActivity activity, DistResumenFragment fragment, List<Pivot> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.fragment = fragment;
        this.QuickContext = context;
        this.data = aListdata;
    }

    public DistrResumenAdapter() {

    }


    public void updateData(List<Pivot> productosList) {
        this.productosList = productosList;
        notifyDataSetChanged();
        cleanVariables();
        activity.cleanTotalize();

    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_distribucion_resumen, parent, false);

        context = parent.getContext();
      /*  slecTAB = activity.getSelecClienteTab();
        if (slecTAB == 1){
            Toast.makeText(QuickContext,"nadaResumen1",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(QuickContext,"nadaResumen0",Toast.LENGTH_LONG).show();

        }*/

        return new DistrResumenAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrResumenAdapter.CharacterViewHolder holder, final int position) {

        final Pivot pivot = productosList.get(position);

        Realm realm = Realm.getDefaultInstance();
        Productos producto = realm.where(Productos.class).equalTo("id", pivot.getProduct_id()).findFirst();
        Venta ventas = realm.where(Venta.class).equalTo("invoice_id", pivot.getInvoice_id()).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();
        realm.close();

        description = producto.getDescription();
        tipo = producto.getProduct_type_id();

        cantidad = Double.parseDouble(pivot.getAmount());
        precio = Double.valueOf(pivot.getPrice());
        descuento = Double.valueOf(pivot.getDiscount());
        clienteFixedDescuento = Double.valueOf(clientes.getFixedDiscount());

        holder.txt_resumen_factura_nombre.setText(description);
        holder.txt_resumen_factura_precio.setText("P: " + precio);
        holder.txt_resumen_factura_descuento.setText("Descuento de: " + descuento);
        holder.txt_resumen_factura_cantidad.setText("C: " + cantidad);

        String pivotTotal = String.format("%,.2f", (precio * cantidad));
        holder.txt_resumen_factura_total.setText("T: " + pivotTotal);
        cleanVariables();
        activity.cleanTotalize();
        totalize();

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_resumen_factura_nombre, txt_resumen_factura_descuento, txt_resumen_factura_precio, txt_resumen_factura_cantidad, txt_resumen_factura_total;
        protected CardView cardView;
        ImageButton btnEliminarResumen;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumen);
            txt_resumen_factura_nombre = (TextView) view.findViewById(R.id.txt_resumen_factura_nombre);
            txt_resumen_factura_descuento = (TextView) view.findViewById(R.id.txt_resumen_factura_descuento);
            txt_resumen_factura_precio = (TextView) view.findViewById(R.id.txt_resumen_factura_precio);
            txt_resumen_factura_cantidad = (TextView) view.findViewById(R.id.txt_resumen_factura_cantidad);
            txt_resumen_factura_total = (TextView) view.findViewById(R.id.txt_resumen_factura_total);
            btnEliminarResumen = (ImageButton) view.findViewById(R.id.btnEliminarResumen);

            btnEliminarResumen.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    cleanVariables();
                    activity.cleanTotalize();
                    int pos = getAdapterPosition();

                    // Updating old as well as new positions
                    notifyItemChanged(selected_position1);
                    selected_position1 = getAdapterPosition();
                    notifyItemChanged(selected_position1);

                    final Pivot clickedDataItem = productosList.get(pos);

                    final int resumenProductoId = clickedDataItem.getId();
                    final double cantidadProducto = Double.parseDouble(clickedDataItem.getAmount());

                    Toast.makeText(view.getContext(), "You clicked " + resumenProductoId, Toast.LENGTH_SHORT).show();

                    // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                    Realm realm3 = Realm.getDefaultInstance();
                    realm3.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm3) {

                            Inventario inventario = realm3.where(Inventario.class).equalTo("product_id", clickedDataItem.getProduct_id()).findFirst();
                            idInvetarioSelec = inventario.getId();
                            amount_dist_inventario = Double.valueOf(inventario.getAmount_dist());
                            realm3.close();
                            Log.d("idinventario", idInvetarioSelec + "");
                        }
                    });

                    // OBTENER NUEVO AMOUNT_DIST
                    final Double nuevoAmountDevuelto = cantidadProducto + amount_dist_inventario;
                    Log.d("nuevoAmount", nuevoAmountDevuelto + "");

                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
                    final Realm realm2 = Realm.getDefaultInstance();
                    realm2.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm2) {
                            Inventario inv_actualizado = realm2.where(Inventario.class).equalTo("id", idInvetarioSelec).findFirst();
                            inv_actualizado.setAmount_dist(String.valueOf(nuevoAmountDevuelto));
                            realm2.insertOrUpdate(inv_actualizado);
                            realm2.close();
                        }
                    });

                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO CREDIT_LIMIT EN LA FACTURA
                /*    final Realm realm5 = Realm.getDefaultInstance();
                    realm5.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm5) {
                            Clientes inv_actualizado = realm5.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();
                            inv_actualizado.setCreditLimit(String.valueOf(credi));
                            realm5.insertOrUpdate(inv_actualizado);
                            realm5.close();
                        }
                    });*/

                    // TRANSACCIÓN BD PARA BORRAR EL CAMPO
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm) {
                            RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("id", resumenProductoId).findAll();
                            result.deleteAllFromRealm();
                            realm.close();
                        }

                    });
                    notifyDataSetChanged();
                    fragment.updateData();

                }
            });
        }

    }

    public void totalize() {
        cleanVariables();
        activity.cleanTotalize();
        if (tipo.equals("1")) {
            subGrab = subGrab + (precio) * (cantidad);
         //   subTotalGrabado = String.format("%,.2f", subGrab);
            Log.d("subTotalGrabado", subTotalGrabado);

            subGrabm = subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)));
            subTotalGrabadoM = String.format("%,.2f", subGrabm);
            Log.d("subTotalGrabadoM", subTotalGrabadoM);
        }
        else {
            subExen = subExen + ((precio) * (cantidad));
           // subTotalExento = String.format("%,.2f", subExen);

        }
        //  TODO REVISAR ANDROIDPOS EL IF QUE REVISA SI ESTA LLENO O NO

        discountBill += ((descuento / 100) * (precio) * (cantidad));

        discountBill += ((subExen * (clienteFixedDescuento / 100.00)) + (subGrabm * (clienteFixedDescuento / 100.00)));
        //descuentoCliente = String.format("%,.2f", discountBill);
      //  Log.d("descuentoCliente", descuentoCliente);


        if (subGrab > 0) {
            IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (iva / 100);
        //    impuestoIVA = String.format("%,.2f", IvaT);

         //   Log.d("impuestoIVA", impuestoIVA);
        }
        else {
            IvaT = 0.0;
            impuestoIVA = String.format("%,.2f", IvaT);
            Log.d("impuestoIVA", impuestoIVA);
        }

        subt = subGrab + subExen;
       // subTotal = String.format("%,.2f", subt);
        Log.d("subtotal", subt + "");
        total = (subt + IvaT) - discountBill;
       // Total = String.format("%,.2f", total);
       // Log.d("total", total + "");

        activity.setTotalizarSubGrabado(subGrab);
        activity.setTotalizarSubExento(subExen);
        activity.setTotalizarSubTotal(subt);
        activity.setTotalizarDescuento(discountBill);

        activity.setTotalizarImpuestoIVA(IvaT);
        activity.setTotalizarTotal(total);
      //  activity.setTotalizarTotalDouble(total);

    }

    private void cleanVariables() {
        subTotalGrabado = "0";
        subTotalExento = "0";
        subTotal = "0";
        descuentoCliente = "0";
        impuestoIVA = "0";
        Total = "0";
        total = 0.0;
    }

    public void clearAll() {
        subGrab = 0.0;
        subGrabm = 0.0;
        subExen = 0.0;
        IvaT = 0.0;
        subt = subGrab + subExen;
        total = subt + IvaT;
        try {
            System.gc();
        }
        catch (Exception e) {
        }


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}

