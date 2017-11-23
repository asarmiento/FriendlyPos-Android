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
    private static Double descuento= 0.0;
    private static Double clienteFixedDescuento = 0.0;


    private static String subTotalGrabado, subTotalGrabadoM, subTotalExento, descuentoCliente, subTotal, Total;

    private static Context QuickContext = null;

    public DistrResumenAdapter(Context context, DistribucionActivity activity, List<Pivot> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.QuickContext = context;
        this.data = aListdata;
    }
    public DistrResumenAdapter() {

    }


    public void updateData(List<Pivot> productosList){
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_distribucion_resumen, parent, false);

        context = parent.getContext();


        return new DistrResumenAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrResumenAdapter.CharacterViewHolder holder, final int position) {
        final Pivot pivot = productosList.get(position);

        //todo repasar esto
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

    public static class CharacterViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_resumen_factura_nombre, txt_resumen_factura_descuento,txt_resumen_factura_precio, txt_resumen_factura_cantidad, txt_resumen_factura_total;
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
                    int pos = getAdapterPosition();
                   // if (pos == RecyclerView.NO_POSITION) return;
                    Log.d("posBorrar", pos+"");
                    // Updating old as well as new positions
                  /*  notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);


                    Venta clickedDataItem = contentList.get(pos);
                    String facturaID = clickedDataItem.getInvoice_id();

                    Realm realm = Realm.getDefaultInstance();
                    //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                    RealmResults<Pivot> facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaID).findAll();

                    realm.close();

                    Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();
                    Log.d("PRODUCTOSFACTURA", facturaid1 + "");
                    activity.setInvoiceId(facturaID);*/

                }
            });
        }

    }

    public void removeItem(Pivot position) {

    /*   if (position.sonsList != null) {
            for (int a : position.sonsList)
                System.out.println("Son Id" + a);

            for (int a : position.sonsList) {
                removeItem(data.get(a));

            }

        }*/

        int pos = data.indexOf(position);
        Log.d("posBorrrar", pos + "");
        /*
        notifyItemRemoved(pos);
        data.remove(pos);
        Double amountDevolver = ProductsBill.get(position.id) - Functions.sGetDecimalStringAnyLocaleAsDouble(position.quantity);
        if (amountDevolver > 0) {
            ProductsBill.put(position.id, ProductsBill.get(position.id) - Integer.parseInt(position.quantity));
        }
        else {

            ProductsBill.remove(position.id);


            // double SUMA2 = position.inventories.amount +  Double.parseDouble(position.quantity);


        }

        Inventories inventory = new Select().all().from(Inventories.class)
                .where(Condition.column(Inventories$Table.PRODUCT_PRODUCT).eq(position.inventories.product.id))
                .querySingle();

        //TODO SUMA PARA DEVOLVER EL INVENTARIO

        inventory.amount = inventory.amount + Double.parseDouble(position.quantity);
        inventory.save();

        new Delete().from(ProductByInvoices.class)
                .where(Condition.column(ProductByInvoices$Table.PRODUCT_PRODUCT).eq(position.inventories.product.id))
                .and(Condition.column(ProductByInvoices$Table.INVOICE_INVOICE).eq(currentInvoice.id))
                .query();

        getDataInventories getdata1 = new getDataInventories();
        getdata1.execute();

        adapterI.notifyDataSetChanged();
        totalize();

        try {
            if (bill_type == 2) {
                creditLm.setText("C.Disponible: " + String.format("%,.2f", getCustomerCreditPending()));
            }
            setTotalFields();
        }
        catch (Exception e) {
        }

        //inventory.put(position.id, inventory.get(position.id) + Integer.parseInt(position.quantity));
        //textExistencia.setText("Existencia en Inv. " + inventory.get(position.id));
        //restotalize(position);*/
    }

   public void totalize() {


               if (tipo.equals("1")) {
                   subGrab = subGrab + (precio) * (cantidad);
                   subTotalGrabado = String.format("%,.2f", subGrab);
                   Log.d("subTotalGrabado", subTotalGrabado);

                   subGrabm = subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)));
                   subTotalGrabadoM = String.format("%,.2f", subGrabm);
                   Log.d("subTotalGrabadoM", subTotalGrabadoM);
               } else {
                   subExen = subExen + ((precio) * (cantidad));
                   subTotalExento = String.format("%,.2f", subExen);
                   Log.d("subTotalExento", subTotalExento);
               }
               //  TODO REVISAR ANDROIDPOS EL IF QUE REVISA SI ESTA LLENO O NO

               discountBill += ((descuento / 100) * (precio) * (cantidad));

           discountBill += ((subExen * (clienteFixedDescuento / 100.00)) + (subGrabm * (clienteFixedDescuento / 100.00)));
           descuentoCliente = String.format("%,.2f", discountBill);
           Log.d("descuentoCliente", descuentoCliente);


           if (subGrab > 0) {
               IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (iva / 100);
               impuestoIVA = String.format("%,.2f", IvaT);

               Log.d("impuestoIVA", impuestoIVA);
           } else {
               IvaT = 0.0;
               impuestoIVA = String.format("%,.2f", IvaT);
               Log.d("impuestoIVA", impuestoIVA);
           }

           subt = subGrab + subExen;
           subTotal = String.format("%,.2f", subt);
           Log.d("subtotal", subt + "");
           total = (subt + IvaT) - discountBill;
           Total = String.format("%,.2f", total);
           Log.d("total", total + "");

       activity.setTotalizarSubGrabado(subTotalGrabado);
       activity.setTotalizarSubExento(subTotalExento);

       activity.setTotalizarSubTotal(subTotal);
       activity.setTotalizarDescuento(descuentoCliente);

       activity.setTotalizarImpuestoIVA(impuestoIVA);
       activity.setTotalizarTotal(Total);

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

