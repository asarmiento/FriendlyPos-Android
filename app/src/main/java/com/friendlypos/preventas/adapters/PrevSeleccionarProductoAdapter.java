package com.friendlypos.preventas.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.TotalizeHelper;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PrevSeleccionarProductoAdapter  {


}

