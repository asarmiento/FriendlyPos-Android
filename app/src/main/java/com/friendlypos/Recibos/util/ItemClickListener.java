package com.friendlypos.Recibos.util;


import android.view.View;

import com.friendlypos.Recibos.modelo.EnviarRecibos;
import com.friendlypos.Recibos.modelo.RecibosResponse;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.distribucion.modelo.MarcasResponse;
import com.friendlypos.distribucion.modelo.MetodoPagoResponse;
import com.friendlypos.distribucion.modelo.TipoProductoResponse;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.login.modelo.UsuariosResponse;
import com.friendlypos.preventas.modelo.BonusesResponse;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;
import com.friendlypos.preventas.modelo.NumeracionResponse;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.EnviarClienteGPS;
import com.friendlypos.principal.modelo.ProductosResponse;
import com.friendlypos.principal.modelo.SysconfResponse;
import com.friendlypos.principal.modelo.customer_location;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ItemClickListener {

    void onItemClick(View v, int pos);

}
