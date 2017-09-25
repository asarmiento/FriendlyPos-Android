package com.friendlypos.login.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.login.modelo.AppResponse;
import com.friendlypos.principal.activity.ClientesActivity;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.activity.ProductosActivity;
import com.friendlypos.principal.adapters.ClientesAdapter;
import com.friendlypos.principal.interfaces.RequestInterface;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login2Activity extends AppCompatActivity {

    @Bind(R.id.textView3)
    TextView textView3;

    @Bind(R.id.usuario)
    EditText usuario;

    @Bind(R.id.contraseña)
    EditText contraseña;


    private ProgressDialog progress;
    private ArrayList<Clientes> mContentsArray = new ArrayList<>();
    private ClientesAdapter adapter;

    private Realm realm;
    private RequestInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2login);
        ButterKnife.bind(this);





}

    public void onClickGo(View component) {

        switch (component.getId()){

            case R.id.login:

                progress = new ProgressDialog(this);
                progress.setMessage("Cargando lista de clientes");
                progress.setCanceledOnTouchOutside(false);
                progress.show();

               String user = usuario.getText().toString();
                String pass = contraseña.getText().toString();


                api = BaseManager.getApi();
                Call<AppResponse> call = api.login(user, pass);

                call.enqueue(new Callback<AppResponse>() {
                    @Override
                    public void onResponse(Call<AppResponse> call, Response<AppResponse> response) {
                        mContentsArray.clear();

                        if(response.isSuccessful()) {
                            progress.dismiss();

                            String respuesta = response.body().getAccess_token();
                            // Add content to the realm DB
                            // Open a transaction to store items into the realm
                            // Use copyToRealm() to convert the objects into proper RealmObjects managed by Realm.
                         /*   realm.beginTransaction();
                            realm.copyToRealmOrUpdate(mContentsArray);
                            realm.commitTransaction();*/
                            textView3.setText(respuesta);

                            Toast.makeText(Login2Activity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        } else {
                            progress.dismiss();
                         /*   Toast.makeText(Login2Activity.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                            RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                            mContentsArray.addAll(results);*/
                        }

/*                        adapter.notifyDataSetChanged();*/
                    }

                    @Override
                    public void onFailure(Call<AppResponse> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(Login2Activity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                        RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                        mContentsArray.addAll(results);
                        adapter.notifyDataSetChanged();
                    }
                });

                break;

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(Login2Activity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
