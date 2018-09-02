package com.friendlypos.preventas.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.fragment.PrevSelecProductoFragment;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class PrevSeleccionarProductoAdapter  extends RecyclerView.Adapter<PrevSeleccionarProductoAdapter.CharacterViewHolder> implements Filterable {

    private Context context;
    public List<Inventario> productosList;
    List<Inventario> countryModels;
    private PreventaActivity activity;
    private PrevSelecProductoFragment fragment;
    private static double producto_amount_dist_add = 0;
    private static double producto_bonus_add = 0;

    private static double producto_descuento_add = 0;
    private static double productosParaObtenerBonus = 0;
    private static double productosDelBonus = 0;
    Date fechaExpiracionBonus;
    private int selected_position = -1;
    static double creditoLimiteCliente = 0.0;
    double totalCredito = 0.0;;
    TotalizeHelperPreventa totalizeHelper;
    int idDetallesFactura = 0;
    int nextId;
    String customer;
    SessionPrefes session;
    Spinner spPrices;
    String idProducto;
    int idFacturaSeleccionada;

    private CustomFilter mFilter;

    public PrevSeleccionarProductoAdapter(PreventaActivity activity, PrevSelecProductoFragment fragment, List<Inventario> productosList) {
        this.activity = activity;
        this.fragment = fragment;
        this.productosList = productosList;
        this.countryModels = new ArrayList<>();
        this.countryModels.addAll(productosList);
        session = new SessionPrefes(getApplicationContext());
        totalizeHelper = new TotalizeHelperPreventa(activity);
        this.mFilter = new CustomFilter(PrevSeleccionarProductoAdapter.this);
    }

    public Filter getFilter() {
        return mFilter;
    }

    public void updateData(List<Inventario> productosList) {
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public PrevSeleccionarProductoAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_preventa_productos, parent, false);
        context = parent.getContext();
        return new PrevSeleccionarProductoAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PrevSeleccionarProductoAdapter.CharacterViewHolder holder, final int position) {

        List<Pivot> pivots = activity.getAllPivotDelegate();
        final Inventario inventario = productosList.get(position);

        Realm realm = Realm.getDefaultInstance();
        Productos producto = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst();

        final String description = producto.getDescription();
        String marca = producto.getBrand_id();
        String tipo = producto.getProduct_type_id();
        String precio = producto.getSale_price();

        String marca2 = realm.where(Marcas.class).equalTo("id", marca).findFirst().getName();
        String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();


        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //  Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", inventario_id).findFirst();
                    //  inv_actualizado.setAmount_dist(String.valueOf(nuevoAmount));
                    inventario.setNombre_producto(description);
                    realm.copyToRealmOrUpdate(inventario); // using insert API

                    Log.d("asda", inventario.getNombre_producto());
                }

            });

        } catch (Exception e) {
            Log.e("error", "error", e);
            Toast.makeText(context,"error", Toast.LENGTH_SHORT).show();

        }

        holder.txt_producto_factura_nombre.setText(description);
        holder.txt_producto_factura_marca.setText("Marca: " + marca2);
        holder.txt_producto_factura_tipo.setText("Tipo: " + tipoProducto);
        holder.txt_producto_factura_precio.setText(precio);
        holder.fillData(producto);

        if(pivots.size() == 0){
            Log.d("jd", "se limpia x 0");
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }
        else{

        for (Pivot pivot: pivots){
       /*  for (int i = 0; i <= pivots.size(); i++){*/
            if(producto.getId().equals(pivot.getProduct_id())){
                Log.d("jd", "seteando color x lista");
                holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
                return;
            }else{
                Log.d("jd", "se limpia");
                holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
            }
        }
        }
        realm.close();
    }
    //}

    public void addProduct(final int inventario_id, final String producto_id,/*  final Double cantidadDisponible, */
    final String description, String Precio1, String Precio2,
                           String Precio3, String Precio4, String Precio5, final String bonusProducto) {

        final invoiceDetallePreventa invoiceDetallePreventa = activity.getCurrentInvoice();


        idDetallesFactura =  invoiceDetallePreventa.getP_id();
        Log.d("FACTURAIDDELEG", idDetallesFactura + "");
        // invoiceDetallePreventa.setP_code(weqweq);

        idFacturaSeleccionada = (activity).getInvoiceIdPreventa();
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada + "");
        idProducto = producto_id;
        Log.d("idProductoSeleccionado", producto_id + "");


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.promptamount, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView txtNombreProducto = (TextView) promptView.findViewById(R.id.txtNombreProducto);
        txtNombreProducto.setText(description);

        final TextView label = (TextView) promptView.findViewById(R.id.promtClabel);
       // label.setText("Escriba una cantidad maxima de " + cantidadDisponible + " minima de 1");
        label.setText("Escriba la cantidad requerida del producto");

        final TextView txtBonificacion = (TextView) promptView.findViewById(R.id.txtBonificacion);

        if (bonusProducto.equals("1")){
            getBoni();
            final Realm realmBonus = Realm.getDefaultInstance();

            realmBonus.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realmBonus) {

                    Bonuses productoConBonus = realmBonus.where(Bonuses.class).equalTo("product_id", Integer.valueOf(producto_id)).findFirst();
                    productosParaObtenerBonus = Double.parseDouble(productoConBonus.getProduct_sale());
                    productosDelBonus = Double.parseDouble(productoConBonus.getProduct_bonus());
                    fechaExpiracionBonus = productoConBonus.getExpiration();

                    Log.d("BONIF", productoConBonus.getProduct_id() +  " "+ productosParaObtenerBonus +  " "+ productosDelBonus +  " "+ String.valueOf(fechaExpiracionBonus));

                }
            });

            txtBonificacion.setVisibility(View.VISIBLE);
            txtBonificacion.setText(" La cantidad para bonificarle es de: " + productosParaObtenerBonus);

        }





        final EditText input = (EditText) promptView.findViewById(R.id.promtCtext);
        final EditText desc = (EditText) promptView.findViewById(R.id.promtCDesc);

        spPrices = (Spinner) promptView.findViewById(R.id.spPrices);
        ArrayList<Double> pricesList = new ArrayList<>();
        final Double precio1 = Double.valueOf(Precio1);
        Double precio2 = Double.valueOf(Precio2);
        Double precio3 = Double.valueOf(Precio3);
        Double precio4 = Double.valueOf(Precio4);
        Double precio5 = Double.valueOf(Precio5);

        pricesList.add(precio1);

        if (precio2 != 0)
            pricesList.add(precio2);

        if (precio3 != 0)
            pricesList.add(precio3);

        if (precio4 != 0)
            pricesList.add(precio4);

        if (precio5 != 0)
            pricesList.add(precio5);

        ArrayAdapter<Double> pricesAdapter = new ArrayAdapter<Double>(label.getContext(), android.R.layout.simple_spinner_item, pricesList);
        spPrices.setAdapter(pricesAdapter);
        spPrices.setSelection(0);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                try {

                    // TODO obtiene la cantidad del producto
                producto_amount_dist_add = Double.parseDouble(((input.getText().toString().isEmpty()) ? "0" : input.getText().toString()));

                    // TODO obtiene el descuento del producto
                producto_descuento_add = Double.parseDouble(((desc.getText().toString().isEmpty()) ? "0" : desc.getText().toString()));


                if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {

                        if (bonusProducto.equals("1")){
                            Log.d("idProductoBONIF", producto_id + "");

                            long fechaexp = fechaExpiracionBonus.getTime();
                            Log.d("fechaExpBONIF", fechaexp + "");

                            Calendar cal = Calendar.getInstance();
                            long hoy = cal.getTimeInMillis();
                            Log.d("fechaBONIF", hoy + "");


                            if(producto_amount_dist_add >= productosParaObtenerBonus ){

                                if(hoy <= fechaexp){
                                    Log.d("PRODOBTE", productosParaObtenerBonus + "");
                                    Log.d("PRODDELBO", productosDelBonus + "");
                                    Log.d("PRODADD", producto_amount_dist_add + "");

                                    double productos = producto_amount_dist_add / productosParaObtenerBonus;

                                    String prod = String.format("%.0f", productos);
                                    double productoBonusTotal = Double.parseDouble(prod) * productosDelBonus;


                                    Log.d("PROD DIV", productos + "");
                                    Log.d("PROD TOTAL", productoBonusTotal + "");

                                    producto_bonus_add =  producto_amount_dist_add + productoBonusTotal;
                                    Log.d("PRODUCTODELBONUS", producto_bonus_add + "");
                                    agregarBonificacion();
                                    Toast.makeText(context, "Se realizó una bonificación de " + productoBonusTotal + " productos", Toast.LENGTH_LONG).show();

                            }
                                else{
                                    Toast.makeText(context, "Fecha expirada para el bonus", Toast.LENGTH_LONG).show();
                                    agregar();
                                }
                            }
                            else{
                                Toast.makeText(context, "No alcanza la cantidad deseada para el bonus", Toast.LENGTH_LONG).show();
                                agregar();
                            }

                        }

                        else{
                            agregar();
                        }

                }
                    else {
                    Toast.makeText(context, "El producto no se agrego, El descuento debe ser >0 <11", Toast.LENGTH_LONG).show();
                }
                notifyDataSetChanged();
                }
                catch (Exception e) {
                    e.printStackTrace();

                }

                }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertD.show();
    }

    public void agregar(){
        session.guardarDatosBonus(0);
        Toast.makeText(context,"Add 0 " + session.getDatosBonus() ,Toast.LENGTH_LONG).show();
        final double precioSeleccionado = (double) spPrices.getSelectedItem();
        Log.d("precioSeleccionado", precioSeleccionado + "");

        //  CREDITO
        String metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id();
        Double cred = Double.parseDouble(activity.getCreditoLimiteClientePreventa());
        if (metodoPagoCliente.equals("1")) {
            creditoLimiteCliente = cred;
            totalCredito = creditoLimiteCliente;
            Log.d("ads", creditoLimiteCliente + "");
        }
        else if (metodoPagoCliente.equals("2")) {
            double totalProducSlecc = precioSeleccionado * producto_amount_dist_add;
            creditoLimiteCliente = cred;
            totalCredito = creditoLimiteCliente - totalProducSlecc;
            Log.d("ads", totalCredito + "");
        }

        // LIMITAR SEGUN EL LIMITE DEL CREDITO
        if (totalCredito >= 0) {
            int numero = session.getDatosPivotPreventa();
            // increment indexrev
            Number currentIdNum = numero;

            if (currentIdNum == null) {
                nextId = 1;
            }
            else {
                nextId = currentIdNum.intValue() + 1;
            }

            Pivot pivotnuevo = new Pivot(); // unmanaged
            pivotnuevo.setId(nextId);
            pivotnuevo.setInvoice_id(String.valueOf(idDetallesFactura));
            pivotnuevo.setProduct_id(idProducto);
            pivotnuevo.setPrice(String.valueOf(precioSeleccionado));
            pivotnuevo.setAmount(String.valueOf(producto_bonus_add));
            pivotnuevo.setDiscount(String.valueOf(producto_descuento_add));
            pivotnuevo.setDelivered(String.valueOf(producto_bonus_add));
            pivotnuevo.setDevuelvo(0);
            pivotnuevo.setBonus(0);
            pivotnuevo.setAmountSinBonus(producto_amount_dist_add);

            activity.insertProduct(pivotnuevo);
            numero++;
            session.guardarDatosPivotPreventa(numero);


            sale ventaDetallePreventa = activity.getCurrentVenta();
            ventaDetallePreventa.getInvoice_id();

            if(ventaDetallePreventa.getInvoice_id().equals(String.valueOf(idFacturaSeleccionada))){

                customer = ventaDetallePreventa.getCustomer_id();

            }

            // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
            final Realm realm4 = Realm.getDefaultInstance();
            realm4.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm4) {

                    //final sale ventas = realm4.where(sale.class).equalTo("invoice_id", idFacturaSeleccionada).findFirst();
                    Clientes clientes = realm4.where(Clientes.class).equalTo("id", customer).findFirst();
                    Log.d("ads", clientes + "");
                    clientes.setCreditLimit(String.valueOf(totalCredito));

                    realm4.insertOrUpdate(clientes); // using insert API

                    realm4.close();
                    activity.setCreditoLimiteClientePreventa(String.valueOf(totalCredito));

                    fragment.updateData();
                    List<Pivot> list = activity.getAllPivotDelegate();
                    activity.cleanTotalize();
                    totalizeHelper = new TotalizeHelperPreventa(activity);
                    totalizeHelper.totalize(list);
                    Log.d("listaResumenADD", list + "");

                }
            });
            //   activity.getAllPivotDelegate();
            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show();
        }

    }

    public void agregarBonificacion(){
        session.guardarDatosBonus(1);
        Toast.makeText(context,"Add 1 " + session.getDatosBonus() ,Toast.LENGTH_LONG).show();
        final double precioSeleccionado = (double) spPrices.getSelectedItem();
        Log.d("precioSeleccionado", precioSeleccionado + "");

        //  CREDITO
        String metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id();
        Double cred = Double.parseDouble(activity.getCreditoLimiteClientePreventa());

        if (metodoPagoCliente.equals("1")) {
            creditoLimiteCliente = cred;
            totalCredito = creditoLimiteCliente;
            Log.d("ads", creditoLimiteCliente + "");
        }
        else if (metodoPagoCliente.equals("2")) {
            double totalProducSlecc = precioSeleccionado * producto_amount_dist_add;
            creditoLimiteCliente = cred;
            totalCredito = creditoLimiteCliente - totalProducSlecc;
            Log.d("ads", totalCredito + "");
        }

        // LIMITAR SEGUN EL LIMITE DEL CREDITO
        if (totalCredito >= 0) {
            int numero = session.getDatosPivotPreventa();
            // increment indexrev
            Number currentIdNum = numero;

            if (currentIdNum == null) {
                nextId = 1;
            }
            else {
                nextId = currentIdNum.intValue() + 1;
            }

            Pivot pivotnuevo = new Pivot(); // unmanaged
            pivotnuevo.setId(nextId);
            pivotnuevo.setInvoice_id(String.valueOf(idDetallesFactura));
            pivotnuevo.setProduct_id(idProducto);
            pivotnuevo.setPrice(String.valueOf(precioSeleccionado));
            pivotnuevo.setAmount(String.valueOf(producto_bonus_add));
            pivotnuevo.setDiscount(String.valueOf(producto_descuento_add));
            pivotnuevo.setDelivered(String.valueOf(producto_bonus_add));
            pivotnuevo.setDevuelvo(0);
            pivotnuevo.setBonus(1);

            activity.insertProduct(pivotnuevo);
            numero++;
            session.guardarDatosPivotPreventa(numero);

            sale ventaDetallePreventa = activity.getCurrentVenta();
            ventaDetallePreventa.getInvoice_id();

            if(ventaDetallePreventa.getInvoice_id().equals(String.valueOf(idFacturaSeleccionada))){

                customer = ventaDetallePreventa.getCustomer_id();

            }

            // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
            final Realm realm4 = Realm.getDefaultInstance();
            realm4.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm4) {

                    //final sale ventas = realm4.where(sale.class).equalTo("invoice_id", idFacturaSeleccionada).findFirst();
                    Clientes clientes = realm4.where(Clientes.class).equalTo("id", customer).findFirst();
                    Log.d("ads", clientes + "");
                    clientes.setCreditLimit(String.valueOf(totalCredito));

                    realm4.insertOrUpdate(clientes); // using insert API

                    realm4.close();
                    activity.setCreditoLimiteClientePreventa(String.valueOf(totalCredito));

                    fragment.updateData();
                    List<Pivot> list = activity.getAllPivotDelegate();
                    activity.cleanTotalize();
                    totalizeHelper = new TotalizeHelperPreventa(activity);
                    totalizeHelper.totalize(list);
                    Log.d("listaResumenADD", list + "");

                }
            });
            //   activity.getAllPivotDelegate();
            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show();
        }

    }

    private List<Bonuses> getBoni(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Bonuses> query = realm.where(Bonuses.class);
        RealmResults<Bonuses> result1 = query.findAll();
        Log.d("BONIFICACION", result1 + "");
        return result1;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }


    /*Filtro*/
    public class CustomFilter extends Filter {
        private PrevSeleccionarProductoAdapter listAdapter;

        private CustomFilter(PrevSeleccionarProductoAdapter listAdapter) {
            super();
            this.listAdapter = listAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            countryModels.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                countryModels.addAll(productosList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final Inventario person : productosList) {
                    if (person.getNombre_producto().toLowerCase().contains(filterPattern)) {
                        countryModels.add(person);
                    }
                }
            }
            results.values = countryModels;
            results.count = countryModels.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void setFilter(List<Inventario> countryModels){

        productosList = new ArrayList<>();
        productosList.addAll(countryModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_producto_factura_nombre, txt_producto_factura_marca, txt_producto_factura_tipo, txt_producto_factura_precio, txt_producto_factura_disponible, txt_producto_factura_seleccionado;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewSeleccionarProductosPreventa);
            txt_producto_factura_nombre = (TextView) view.findViewById(R.id.txt_prev_producto_factura_nombre);
            txt_producto_factura_marca = (TextView) view.findViewById(R.id.txt_prev_producto_factura_marca);
            txt_producto_factura_tipo = (TextView) view.findViewById(R.id.txt_prev_producto_factura_tipo);
            txt_producto_factura_precio = (TextView) view.findViewById(R.id.txt_prev_producto_factura_precio);
           /* txt_producto_factura_disponible = (TextView) view.findViewById(R.id.txt_prev_producto_factura_disponible);*/
            txt_producto_factura_seleccionado = (TextView) view.findViewById(R.id.txt_prev_producto_factura_seleccionado);
        }

        void fillData(final Productos producto) {
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    session.guardarDatosBonus(0);



                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    Inventario clickedDataItem = productosList.get(pos);
                    String ProductoID = clickedDataItem.getProduct_id();
                    int InventarioID = clickedDataItem.getId();

                    String precio = producto.getSale_price();
                    String precio2 = producto.getSale_price2();
                    String precio3 = producto.getSale_price3();
                    String precio4 = producto.getSale_price4();
                    String precio5 = producto.getSale_price5();
                    String bonusProducto = producto.getBonus();

                  /*  Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount());*/

                    Realm realm1 = Realm.getDefaultInstance();
                    Productos producto = realm1.where(Productos.class).equalTo("id", ProductoID).findFirst();

                    String description = producto.getDescription();

                    realm1.close();

                   addProduct(InventarioID, ProductoID, /*ProductoAmount,*/ description, precio, precio2, precio3, precio4, precio5, bonusProducto);
                    Toast.makeText(context,"Add limpia " + session.getDatosBonus() ,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

