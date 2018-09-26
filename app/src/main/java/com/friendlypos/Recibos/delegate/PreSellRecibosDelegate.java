package com.friendlypos.Recibos.delegate;

import android.util.Log;

import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.receiptsDetalle;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class PreSellRecibosDelegate {

    RecibosActivity recibosActivity;

    receiptsDetalle newRecibo;
    List<recibos> productofacturas;

    public PreSellRecibosDelegate(RecibosActivity recibosActivity) {
        this.recibosActivity = recibosActivity;
        productofacturas = new ArrayList<>();
    }

    public void initReciboDetalle(String receipts_id, String customer_id, String reference,
                                  String date, String sum, double balance, String notes) {

        newRecibo = new receiptsDetalle();

        newRecibo.setD_receipts_id(receipts_id);
        newRecibo.setD_customer_id(customer_id);
        newRecibo.setD_reference(reference);
        newRecibo.setD_date(date);
        newRecibo.setD_sum(sum);
        newRecibo.setD_balance(balance);
        newRecibo.setD_notes(notes);
        newRecibo.setD_listaRecibos(productofacturas);

        Log.d("invoice1", newRecibo + "");

    }

    public receiptsDetalle getCurrentRecibos() {
        return newRecibo;
    }


    public void insertRecibo(recibos recibo) {
        productofacturas.add(recibo);

        Log.d("recibo2", productofacturas + "");
    }



    public List<recibos> initRecibo(int pos) {

        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio");
            // send = "No hay invoice emitidas";
        }
        else {
            int abonado = productofacturas.get(pos).getAbonado();

            Log.d("invoiceDev", abonado + "");
            if (abonado == 1) {

                productofacturas.get(pos).setAbonado(0);

            }
            else {
                //  productofacturas.get(pos).removeAllChangeListeners();
                Log.d("vacio", "No hay productos");            }
        }
        Log.d("invoiceremover", productofacturas + "");
        return productofacturas;
    }


    public List<recibos> getAllRecibos() {

        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio");
            // send = "No hay invoice emitidas";
        }
        else {

            for (int i = 0; i < productofacturas.size(); i++) {

                int abonado = productofacturas.get(i).getAbonado();
                Log.d("invoiceDev", abonado + "");
                if (abonado == 1) {

                    productofacturas.get(i);

                }
                else {
                    productofacturas.remove(i);
                    Log.d("vacio", "No hay productos");
                }

            }
        }
        Log.d("invoice4", productofacturas + "");
        return productofacturas;
    }


    public void destroy() {
        this.recibosActivity = null;
        this.newRecibo = null;
    }

    public receipts getReceiptsByReceiptsDetalle() {

        receipts recibo = new receipts();

        recibo.setReceipts_id(String.valueOf(newRecibo.getD_receipts_id()));
        recibo.setCustomer_id(newRecibo.getD_customer_id());
        recibo.setReference(newRecibo.getD_reference());
        recibo.setDate(newRecibo.getD_date());
        recibo.setSum(newRecibo.getD_sum());
        recibo.setBalance(newRecibo.getD_balance());
        recibo.setNotes(newRecibo.getD_notes());

        RealmList<recibos> results = new RealmList<>();

        results.addAll(productofacturas);
        recibo.setListaRecibos(results);


        Log.d("CREAR RECIBO", recibo + "");

        return recibo;
    }
}
