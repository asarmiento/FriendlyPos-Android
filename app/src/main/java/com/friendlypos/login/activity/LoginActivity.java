package com.friendlypos.login.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.friendlypos.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        HtmlTextView copy = (HtmlTextView) findViewById(R.id.copyright);
        copy.setHtmlFromString("<font size=\"7sp\"><a href=\"http://www.sistemasamigables.com/\">" +"</a></font>", new HtmlTextView.LocalImageGetter());

    }


}
