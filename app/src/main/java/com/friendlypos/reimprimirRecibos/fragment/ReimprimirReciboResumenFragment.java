package com.friendlypos.reimprimirRecibos.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reimprimirRecibos.activity.ReimprimirRecibosActivity;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ReimprimirReciboResumenFragment extends BaseFragment {

    public HtmlTextView text;
    receipts recibo_actualizado;
    @Bind(R.id.btnReimprimirReciboNuevo)
    public ImageButton btnReimprimirFactura;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    ReimprimirRecibosActivity activity;
    String facturaId = "";
    int slecTAB;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(getContext());
        activity = new ReimprimirRecibosActivity();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reimprimir_recibo_resumen, container, false);
        text = (HtmlTextView) rootView.findViewById(R.id.html_textReciboNuevo);
        ButterKnife.bind(this, rootView);


        btnReimprimirFactura.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {

                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View promptView = layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null);

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setView(promptView);
                    final CheckBox checkbox = (CheckBox) promptView.findViewById(R.id.checkbox);

                    final TextView label = (TextView) promptView.findViewById(R.id.promtClabelRecibosImp);
                    label.setText("Escriba el número de impresiones requeridas");

                    final EditText input = (EditText) promptView.findViewById(R.id.promtCtextRecibosImp);

                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            String cantidadImpresiones = input.getText().toString();

                            PrinterFunctions.imprimirReimpRecibosTotal(recibo_actualizado, getActivity(), 1, cantidadImpresiones);
                            Toast.makeText(getActivity(), "Reimprimir recibo", Toast.LENGTH_SHORT).show();
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
                else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                    Functions.CreateMessage(getActivity(), "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                }
            }
        });

        return rootView;
    }


    private void getHtmlPreview() {
        try {

            Realm realm = Realm.getDefaultInstance();
            Clientes clientes = realm.where(Clientes.class).equalTo("id", recibo_actualizado.getCustomer_id()).findFirst();

            String nombreCliente = clientes.getFantasyName();
            Log.d("nombreCliente", nombreCliente);
            String preview = "";


            if (recibo_actualizado != null) {


                preview += "<h5>" + "Recibos" + "</h5>";

                preview += "<a><b>A nombre de:</b> " + nombreCliente + "</a><br><br>";
             /*   preview += "<a><b>" + padRight("# Referencia", 25) + padRight("# Factura", 25)+ "</b></a><br>";
                preview += "<a><b>" + padRight("Monto total", 25) + padRight("Monto Pagado", 25)+ "</b></a><br>";
                preview += "<a><b>" + padRight("Total en abonos", 35) + "</b></a><br>";
                preview += "<a><b>" + padRight("Total restante", 35) + "</b></a><br>";*/
               /* preview += "<a><b>" + padRight("Monto total", 35)+ "</b></a><br>";
                preview += "<a><b>" + padRight("Monto Pagado", 10) + padRight("Monto restante", 10) + "</b></a><br>";*/

                preview += "<a>------------------------------------------------<a><br>";

                preview += getPrintDistTotal(recibo_actualizado.getCustomer_id());

            }
            else {
                preview += "<center><h2>Seleccione la factura a ver</h2></center>";
            }
            text.setHtmlFromString(preview, new HtmlTextView.LocalImageGetter());
        }
        catch (Exception e) {
            String preview = "<center><h2>Seleccione la factura a ver cath</h2></center>";
            text.setHtmlFromString(preview, new HtmlTextView.LocalImageGetter());
            Log.d("adsdad", e.getMessage());
        }
    }

    public static String padRight(String s, double n) {
        String centeredString;
        double pad = (n + 4) - s.length();

        if (pad > 0) {
            String pd = Functions.paddigTabs((int) (pad / 2.0));
            centeredString = "\t" + s + "\t" + pd;
            System.out.println("pad: " + "|" + centeredString + "|");
        }
        else {
            centeredString = "\t" + s + "\t";
        }
        return centeredString;
    }

    private String getPrintDistTotal(String idVenta) {
        String send = "";

        Realm realm1 = Realm.getDefaultInstance();
        RealmResults<receipts> result = realm1.where(receipts.class).equalTo("customer_id", idVenta).equalTo("reference",recibo_actualizado.getReference()).findAll();
        Log.d("recibosresult", result+"");
        if (result.isEmpty()) {
            send = "No hay recibos emitidos";
        }
        else {

                List<receipts> salesList1 = realm1.where(receipts.class).equalTo("customer_id", idVenta)
                        .equalTo("reference",recibo_actualizado.getReference()).findAll();

                Log.d("getReference", salesList1.get(0).getReference());

               recibos recibos = realm1.where(recibos.class).equalTo("numeration", salesList1.get(0).getNumeration()).findFirst();

                Log.d("getNumeration", recibos.getNumeration() +"");

                String numeroReferenciaReceipts = salesList1.get(0).getReference();
                String numeracionReceipts = salesList1.get(0).getNumeration();
                double pagadoReceipts =  salesList1.get(0).getMontoCanceladoPorFactura();
                String pagadoSReceipts = String.format("%,.2f", pagadoReceipts);

                double total = recibos.getTotal();
                String totalS = String.format("%,.2f", total);

                double restante = salesList1.get(0).getPorPagarReceipts();
                    String restanteS = String.format("%,.2f", restante);

                double totalAbonos = salesList1.get(0).getBalance();
                String totalAbonosS = String.format("%,.2f", totalAbonos);

               send += /*"# Referencia" + padRight(numeroReferenciaReceipts, 20) + "# Factura" + padRight(numeracionReceipts, 20)+ "<br>" +
                       "Monto total" + padRight(totalS, 40) + "Monto Pagado" + padRight(pagadoSReceipts, 40) + "<br>" +*/

                       "<a><b>" +  padRight("# Referencia:", 20 )+"</b></a>"+ padRight(numeroReferenciaReceipts, 20) + "<br>" +
                       "<a><b>" + padRight("# Factura:", 30 ) +"</b></a>"+ padRight(numeracionReceipts, 20) + "<br>" +
                       "<a><b>" + padRight("Monto Pagado:", 20 ) +"</b></a>" + padRight(pagadoSReceipts, 20) + "<br>" +
                       "<a><b>" + padRight("Monto total:", 30 ) +"</b></a>" + padRight(totalS, 20) + "<br>" +
                       "<a><b>" + padRight("Total en abonos:", 20 ) + "</b></a>" +padRight(totalAbonosS, 20) + "<br>" +
                        "<a><b>" + padRight("Total restante:", 25 ) +"</b></a>" + padRight(restanteS, 20)+"<br>";

               send += "<a>------------------------------------------------<a><br>";

                Log.d("FACTPRODTODFAC", send + "");


            realm1.close();
        }
        return send;
    }

    public void updateData() {
        slecTAB = ((ReimprimirRecibosActivity) getActivity()).getSelecReciboTab();
        if (slecTAB == 1) {

            facturaId =  ((ReimprimirRecibosActivity) getActivity()).getInvoiceIdReimprimirRecibo();


            final Realm realm3 = Realm.getDefaultInstance();
            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {

                    recibo_actualizado = realm3.where(receipts.class).equalTo("reference", facturaId).findFirst();

                    realm3.close();
                }
            });


            getHtmlPreview();
            Log.d("FACTURAIDTOTALIZAR", recibo_actualizado.getCustomer_id()+"");
            Log.d("FACTURAIDTOTALIZAR", facturaId);
        }
        else {
            Log.d("nadaTotalizarupdate",  "nadaTotalizarupdate");
        }

    }
}


