package com.friendlypos.application.runModelo;

import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;

/**
 * Created by Brian on 11/10/16.
 */

public class RunSale {
    public Venta sale;
    public Facturas invoices;
   // public Usuarios users;
    public Clientes customer;

    public RunSale(Venta sal){
        sale = sal;
        invoices = sale.facturas;
    //    users = invoices.user;
        customer = sale.clientes;
    }

}
