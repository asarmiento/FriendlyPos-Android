package com.friendlypos.ventadirecta.adapters;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.friendlypos.ventadirecta.fragment.VentaDirSelecProductoFragment;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;
import com.friendlypos.ventadirecta.util.TotalizeHelperVentaDirecta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class VentaDirSeleccionarProductoAdapter  extends RecyclerView.Adapter<VentaDirSeleccionarProductoAdapter.CharacterViewHolder> {

    private Context context;
    public List<Inventario> productosList;
    private VentaDirectaActivity activity;
    private VentaDirSelecProductoFragment fragment;

    private static double prod_mostrar = 0;

    private static double producto_amount_dist_add = 0;
    private static double producto_descuento_add = 0;
    private static double productosParaObtenerBonus = 0;
    private static double producto_bonus_add = 0;
    private static double productosDelBonus = 0;
    Date fechaExpiracionBonus;
    private int selected_position = -1;
    static double creditoLimiteCliente = 0.0;
    double totalCredito = 0.0;;
    TotalizeHelperVentaDirecta totalizeHelper;
    int idDetallesFactura = 0;
    int nextId;
    String customer;
    SessionPrefes session;
    Spinner spPrices;
    String idProducto;
    int idFacturaSeleccionada, idInventario;
    double d_cantidadDisponible;

    public VentaDirSeleccionarProductoAdapter(VentaDirectaActivity activity, VentaDirSelecProductoFragment fragment, List<Inventario> productosList2) {
        this.activity = activity;
        this.fragment = fragment;
        this.productosList = productosList2;
        session = new SessionPrefes(getApplicationContext());
        totalizeHelper = new TotalizeHelperVentaDirecta(activity);
    }

    public void updateData(List<Inventario> productosList) {
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public VentaDirSeleccionarProductoAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_ventadirecta_productos, parent, false);
        context = parent.getContext();
        return new VentaDirSeleccionarProductoAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VentaDirSeleccionarProductoAdapter.CharacterViewHolder holder, final int position) {
        List<Pivot> pivots = activity.getAllPivotDelegate();
        final Inventario inventario = productosList.get(position);

        Realm realm = Realm.getDefaultInstance();
        Productos producto = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst();

        Log.d("inventario", inventario+ "");

        final String description = producto.getDescription();
        String marca = producto.getBrand_id();
        String tipo = producto.getProduct_type_id();
        String status = producto.getStatus();
        String precio = producto.getSale_price();
        String marca2 = realm.where(Marcas.class).equalTo("id", marca).findFirst().getName();
        String tipoProducto;
        Double impuesto = producto.getIva();

        if(impuesto == 0.0){

            tipoProducto = "Exento";
        }else{
            tipoProducto = "Gravado";
        }
        // String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();

        holder.fillData(producto);
        holder.txt_producto_factura_nombre.setText(description);
        holder.txt_producto_factura_marca.setText("Marca: " + marca2);
        holder.txt_producto_factura_tipo.setText("Tipo: " + tipoProducto);
        holder.txt_producto_factura_precio.setText(precio);
        holder.txt_producto_factura_disponible.setText("Disp: " + inventario.getAmount());



        Log.d("productosListActivo", "" + productosList);

        if (pivots.size() == 0) {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {

            for (Pivot pivot : pivots) {
                if (producto.getId().equals(pivot.getProduct_id())) {
                    holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"));
                    return;
                } else {
                    holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

        }

        if (status.equals("Activo")) { holder.cardView.setVisibility(View.VISIBLE);
        }

        else{
            holder.cardView.setVisibility(View.GONE);
            holder.cardView.getLayoutParams().height = 0;
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(0, 0,0, 0);
            holder.cardView.requestLayout();
            Log.d("inactivo", "inactivo");
        }



        realm.close();
    }

    public void addProduct(final int inventario_id, final String producto_id, final
    Double cantidadDisponible, final String description, String Precio1, String Precio2,
                           String Precio3, String Precio4, String Precio5, final String bonusProducto) {

        final invoiceDetalleVentaDirecta invoiceDetallePreventa = activity.getCurrentInvoice();


        idDetallesFactura =  invoiceDetallePreventa.getP_id();
        Log.d("FACTURAIDDELEG", idDetallesFactura + "");

        idFacturaSeleccionada = (activity).getInvoiceIdVentaDirecta();
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada + "");
        Log.d("idProductoSeleccionado", producto_id + "");
        idProducto = producto_id;
        idInventario = inventario_id;
        d_cantidadDisponible = cantidadDisponible;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.promptamount, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView txtNombreProducto = (TextView) promptView.findViewById(R.id.txtNombreProducto);
        txtNombreProducto.setText(description);

        final TextView label = (TextView) promptView.findViewById(R.id.promtClabel);
        label.setText("Escriba una cantidad maxima de " + cantidadDisponible + " minima de 1");
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
        Double precio1 = Double.valueOf(Precio1);
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

                    producto_amount_dist_add = Double.parseDouble(((input.getText().toString().isEmpty()) ? "0" : input.getText().toString()));
                    producto_descuento_add = Double.parseDouble(((desc.getText().toString().isEmpty()) ? "0" : desc.getText().toString()));

                    if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {

                        if (producto_amount_dist_add > 0 && producto_amount_dist_add <= cantidadDisponible) {

                            if (bonusProducto.equals("1")) {
                                Log.d("idProductoBONIF", producto_id + "");

                                long fechaexp = fechaExpiracionBonus.getTime();
                                Log.d("fechaExpBONIF", fechaexp + "");

                                Calendar cal = Calendar.getInstance();
                                long hoy = cal.getTimeInMillis();
                                Log.d("fechaBONIF", hoy + "");

                                if (producto_amount_dist_add >= productosParaObtenerBonus) {
                                    if (hoy <= fechaexp) {

                                        // TODO PRODUCTO QUE SE AGREGO
                                        Log.d("PROD_AGREGO", producto_amount_dist_add + "");

                                        //  TODO PARA TENER BONIFICACION OCUPA
                                        Log.d("PROD_OCUPA", productosParaObtenerBonus + "");

                                        //  TODO DIVIDIR AGREGADO Y PROD DEL BONUS
                                        double productos = producto_amount_dist_add / productosParaObtenerBonus;

                                        String prod = String.format("%.0f", productos);
                                        double productoBonusTotal = Double.parseDouble(prod) * productosDelBonus;

                                        //  TODO PRODUCTOS QUE SE BONIFICAN GRATIS
                                        Log.d("PROD_GRATIS", productoBonusTotal + "");


                                        producto_bonus_add = producto_amount_dist_add + productoBonusTotal;

                                        Toast.makeText(context, "Se realizó una bonificación de " + productoBonusTotal + " productos", Toast.LENGTH_LONG).show();
                                        agregarBonificacion();


                                    } else {
                                        Toast.makeText(context, "Fecha expirada para el bonus", Toast.LENGTH_LONG).show();
                                        agregar();
                                    }
                                } else {
                                    Toast.makeText(context, "No alcanza la cantidad deseada para el bonus", Toast.LENGTH_LONG).show();
                                    agregar();
                                }

                            } else {
                                agregar();
                            }

                        } else {
                            Toast.makeText(context, "El producto no se agrego, verifique la cantidad que esta ingresando", Toast.LENGTH_LONG).show();
                        }
                    } else {
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

    private List<Bonuses> getBoni(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Bonuses> query = realm.where(Bonuses.class);
        RealmResults<Bonuses> result1 = query.findAll();
        Log.d("BONIFICACION", result1 + "");
        return result1;
    }

    public void agregar(){


        final double precioSeleccionado = (double) spPrices.getSelectedItem();
        Log.d("precioSeleccionado", precioSeleccionado + "");

        String metodoPagoCliente = invoiceDetalleVentaDirecta.getP_payment_method_id();
        Double cred = Double.parseDouble(activity.getCreditoLimiteClienteVentaDirecta());

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

            int numero = session.getDatosPivotVentaDirecta();
            // increment index
            Number currentIdNum1 = numero;

            if (currentIdNum1 == null) {
                nextId = 1;
            }
            else {
                nextId = currentIdNum1.intValue() + 1;
            }


            Pivot pivotnuevo = new Pivot(); // unmanaged
            pivotnuevo.setId(nextId);
            pivotnuevo.setInvoice_id(String.valueOf(idDetallesFactura));
            pivotnuevo.setProduct_id(idProducto);
            pivotnuevo.setPrice(String.valueOf(precioSeleccionado));
            pivotnuevo.setAmount(String.valueOf(producto_amount_dist_add));
            pivotnuevo.setDiscount(String.valueOf(producto_descuento_add));
            pivotnuevo.setDelivered(String.valueOf(producto_amount_dist_add));
            pivotnuevo.setDevuelvo(0);
            pivotnuevo.setBonus(0);
            Log.d("pivotnuevo", pivotnuevo + "");
            activity.insertProduct(pivotnuevo);
          //  numero++;
            session.guardarDatosPivotVentaDirecta(nextId);

            final Double nuevoAmount = d_cantidadDisponible - producto_amount_dist_add;
            Log.d("nuevoAmount", nuevoAmount + "");


            final Realm realm3 = Realm.getDefaultInstance();

            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {

                    Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", idInventario).findFirst();
                    inv_actualizado.setAmount(String.valueOf(nuevoAmount));

                    realm3.insertOrUpdate(inv_actualizado); // using insert API
                }
            });


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
                    activity.setCreditoLimiteClienteVentaDirecta(String.valueOf(totalCredito));

                    fragment.updateData();
                    List<Pivot> list = activity.getAllPivotDelegate();
                    activity.cleanTotalize();
                    totalizeHelper = new TotalizeHelperVentaDirecta(activity);
                    totalizeHelper.totalize(list);
                    Log.d("listaResumenADD", list + "");

                }
            });
            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show();
        }
    }

    public void agregarBonificacion(){

        final double precioSeleccionado = (double) spPrices.getSelectedItem();
        Log.d("precioSeleccionado", precioSeleccionado + "");

        //  CREDITO
        String metodoPagoCliente = invoiceDetalleVentaDirecta.getP_payment_method_id();
        Double cred = Double.parseDouble(activity.getCreditoLimiteClienteVentaDirecta());

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
            int numero = session.getDatosPivotVentaDirecta();
            // increment index
            Number currentIdNum1 = numero;

            if (currentIdNum1 == null) {
                nextId = 1;
            }
            else {
                nextId = currentIdNum1.intValue() + 1;
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
            pivotnuevo.setAmountSinBonus(producto_amount_dist_add);

            activity.insertProduct(pivotnuevo);
            session.guardarDatosPivotVentaDirecta(nextId);

            final Double nuevoAmount = d_cantidadDisponible - producto_bonus_add;
            Log.d("nuevoAmount", nuevoAmount + "");

/*
            final Realm realm3 = Realm.getDefaultInstance();

            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {

                    Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", idInventario).findFirst();
                    inv_actualizado.setAmount(String.valueOf(nuevoAmount));

                    realm3.insertOrUpdate(inv_actualizado); // using insert API
                }
            });

*/
            sale ventaDetallePreventa = activity.getCurrentVenta();
            ventaDetallePreventa.getInvoice_id();

            if(ventaDetallePreventa.getInvoice_id().equals(String.valueOf(idFacturaSeleccionada))){

                customer = ventaDetallePreventa.getCustomer_id();

            }

            // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
            final Realm realmPB = Realm.getDefaultInstance();
            realmPB.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realmPB) {

                    //final sale ventas = realm4.where(sale.class).equalTo("invoice_id", idFacturaSeleccionada).findFirst();
                    Clientes clientes = realmPB.where(Clientes.class).equalTo("id", customer).findFirst();
                    Log.d("ads", clientes + "");
                    clientes.setCreditLimit(String.valueOf(totalCredito));

                    realmPB.insertOrUpdate(clientes); // using insert API

                    realmPB.close();
                    activity.setCreditoLimiteClienteVentaDirecta(String.valueOf(totalCredito));

                    fragment.updateData();
                    List<Pivot> list = activity.getAllPivotDelegate();
                    activity.cleanTotalize();
                    totalizeHelper = new TotalizeHelperVentaDirecta(activity);
                    totalizeHelper.totalize(list);
                    Log.d("listaResumenADD", list + "");
                    prod_mostrar = 0;

                }
            });
            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public void setFilter(List<Inventario> countryModels){

        productosList = new ArrayList<>();
        productosList.addAll(countryModels);
        notifyDataSetChanged();
        Log.d("country", countryModels + "");
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_producto_factura_nombre, txt_producto_factura_marca, txt_producto_factura_tipo, txt_producto_factura_precio, txt_producto_factura_disponible, txt_producto_factura_seleccionado;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewSeleccionarProductosVentaDirecta);
            txt_producto_factura_nombre = (TextView) view.findViewById(R.id.txt_ventadir_producto_factura_nombre);
            txt_producto_factura_marca = (TextView) view.findViewById(R.id.txt_ventadir_producto_factura_marca);
            txt_producto_factura_tipo = (TextView) view.findViewById(R.id.txt_ventadir_producto_factura_tipo);
            txt_producto_factura_precio = (TextView) view.findViewById(R.id.txt_ventadir_producto_factura_precio);
            txt_producto_factura_disponible = (TextView) view.findViewById(R.id.txt_ventadir_producto_factura_disponible);
            txt_producto_factura_seleccionado = (TextView) view.findViewById(R.id.txt_ventadir_producto_factura_seleccionado);
        }

        void fillData(final Productos producto) {
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

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

                    Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount());

                    Realm realm1 = Realm.getDefaultInstance();
                    Productos producto = realm1.where(Productos.class).equalTo("id", ProductoID).findFirst();

                    String description = producto.getDescription();

                    realm1.close();

                    addProduct(InventarioID, ProductoID, ProductoAmount, description, precio, precio2, precio3, precio4, precio5, bonusProducto);

                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




}

