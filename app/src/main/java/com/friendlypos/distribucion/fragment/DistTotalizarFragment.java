package com.friendlypos.distribucion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;

import static java.lang.String.valueOf;


public class DistTotalizarFragment extends BaseFragment {

    private static TextView subGra;
    private static TextView subExe;
    private static TextView subT;
    private static TextView ivaSub;
    private static TextView discount;
    private static TextView Total;
    private static EditText paid;
    private static TextView change;
    private static EditText notes;
    private static Button applyBill;
    private static Button printBill;
    private static EditText client_name;
    private static EditText client_card;
    private static EditText client_contact;
    private static int apply_done = 0;
    private static Context QuickContext = null;
    private static CoordinatorLayout coordinatorLayout;


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_distribucion_totalizar, container, false);

        client_card = (EditText) rootView.findViewById(R.id.client_card);
        client_contact = (EditText) rootView.findViewById(R.id.client_contact);
        client_name = (EditText) rootView.findViewById(R.id.client_name);

        subExe = (TextView) rootView.findViewById(R.id.subExento);
        subGra = (TextView) rootView.findViewById(R.id.subGrabado);
        ivaSub = (TextView) rootView.findViewById(R.id.IvaFact);
        subT = (TextView) rootView.findViewById(R.id.subTotal);
        discount = (TextView) rootView.findViewById(R.id.Discount);
        Total = (TextView) rootView.findViewById(R.id.Total);

        paid = (EditText) rootView.findViewById(R.id.txtPaid);
        change = (TextView) rootView.findViewById(R.id.txtChange);
        notes = (EditText) rootView.findViewById(R.id.txtNotes);


        return rootView;
    }

    @Override
    public void updateData() {
        String totalGrabado = ((DistribucionActivity) getActivity()).getTotalizarSubGrabado();
        String totalExento = ((DistribucionActivity) getActivity()).getTotalizarSubExento();

        String totalSubtotal = ((DistribucionActivity) getActivity()).getTotalizarSubTotal();
        String totalDescuento = ((DistribucionActivity) getActivity()).getTotalizarDescuento();
        String totalImpuesto = ((DistribucionActivity) getActivity()).getTotalizarImpuestoIVA();
        String totalTotal = ((DistribucionActivity) getActivity()).getTotalizarTotal();

        subGra.setText(totalGrabado);
        subExe.setText(totalExento);

        subT.setText(totalSubtotal);
        discount.setText(totalDescuento);

        ivaSub.setText(totalImpuesto);
        Total.setText(totalTotal);

    }


}