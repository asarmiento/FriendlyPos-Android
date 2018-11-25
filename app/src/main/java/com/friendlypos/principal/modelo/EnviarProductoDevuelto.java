package com.friendlypos.principal.modelo;

import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.preventas.modelo.visit;

/**
 * Created by DelvoM on 19/11/2018.
 */

public class EnviarProductoDevuelto {

   private Inventario pivot;

    public EnviarProductoDevuelto(Inventario pivot) {
        this.pivot = pivot;
    }
}
