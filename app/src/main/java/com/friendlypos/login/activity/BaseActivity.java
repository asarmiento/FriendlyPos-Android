package com.friendlypos.login.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.friendlypos.R;
import com.friendlypos.login.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BaseActivity extends AppCompatActivity {
    private static final String TOOLBAR_TITLE = "";
    private static final int ZERO = 0;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.toolbar_title)
    TextView mToolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mToolbar.setTitle(TOOLBAR_TITLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getSupportActionBar() == null) {
            if (mToolbar != null) {
                setupToolbar(mToolbar);
            }
        }
    }

    public void setupToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            mToolbar = toolbar;
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    BaseActivity.this.onBackPressed();
                }
            });

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeActionContentDescription(getString(R.string.back_button_label));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    public void showNoInternetConnectionMessage(String tag) {
        Log.e(tag, getString(R.string.no_internet_connection));
        Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }


    public void replaceCurrentFragment(BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
    }

    public void enableDisableBackButtonToolbar(boolean enable) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(enable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);

    }

    public void setToolBarTitle(String title) {
        mToolBarTitle.setText(title);
    }
}