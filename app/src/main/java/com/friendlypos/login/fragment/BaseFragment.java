package com.friendlypos.login.fragment;

import android.app.Activity;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.app.savior.R;

/**
 * Created by juandiegoGL on 4/25/17.
 */

public class BaseFragment extends Fragment {

    public boolean onBackPressed() {
        return true;
    }

    public void closeSoftKeyboardIfVisible() {
        if (isAdded()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focussedView = getActivity().getCurrentFocus();
            if (focussedView != null) {
                IBinder windowToken = focussedView.getWindowToken();
                if (windowToken != null) {
                    inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
                }
            }
        }
    }

    public void unknown_error(String tag){
        Log.e(tag, getString(R.string.unknown_error));
        Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
    }

    public void replaceCurrentFragment(BaseFragment fragment) {
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container,
                fragment)
            .addToBackStack(null).commit();
    }
}