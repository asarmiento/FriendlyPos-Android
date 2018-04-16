package com.friendlypos.preventas.modelo;

import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 19/03/2018.
 */

public class invoiceDetallePreventa {

    private int p_id;
    private String p_branch_office_id;
    private String p_numeration;

    private double p_longitud;

    private double p_latitud;

    private String p_date;

    private String p_times;

    private String p_date_presale;

    private String p_time_presale;

    private String p_due_date;

    private String p_subtotal;

    private String p_subtotal_taxed;
    private String p_subtotal_exempt;
    private String p_discount;

    private String p_percent_discount;

    private String p_tax;

    private String p_total;

    private String p_changing;
    private String p_note;

    private String p_canceled;
    private String p_paid_up;

    private String p_paid;

    private String p_created_at;

    private String p_user_id;

    private String p_user_id_applied;

    private String p_invoice_type_id;

    private String p_payment_method_id;

    private sale p_sale;

    private RealmList<Pivot> p_productofacturas;

    private int facturaDePreventa = 0;


    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_branch_office_id() {
        return p_branch_office_id;
    }

    public void setP_branch_office_id(String p_branch_office_id) {
        this.p_branch_office_id = p_branch_office_id;
    }

    public String getP_numeration() {
        return p_numeration;
    }

    public void setP_numeration(String p_numeration) {
        this.p_numeration = p_numeration;
    }

    public double getP_longitud() {
        return p_longitud;
    }

    public void setP_longitud(double p_longitud) {
        this.p_longitud = p_longitud;
    }

    public double getP_latitud() {
        return p_latitud;
    }

    public void setP_latitud(double p_latitud) {
        this.p_latitud = p_latitud;
    }

    public String getP_date() {
        return p_date;
    }

    public void setP_date(String p_date) {
        this.p_date = p_date;
    }

    public String getP_times() {
        return p_times;
    }

    public void setP_times(String p_times) {
        this.p_times = p_times;
    }

    public String getP_date_presale() {
        return p_date_presale;
    }

    public void setP_date_presale(String p_date_presale) {
        this.p_date_presale = p_date_presale;
    }

    public String getP_time_presale() {
        return p_time_presale;
    }

    public void setP_time_presale(String p_time_presale) {
        this.p_time_presale = p_time_presale;
    }

    public String getP_due_date() {
        return p_due_date;
    }

    public void setP_due_date(String p_due_date) {
        this.p_due_date = p_due_date;
    }

    public String getP_subtotal() {
        return p_subtotal;
    }

    public void setP_subtotal(String p_subtotal) {
        this.p_subtotal = p_subtotal;
    }

    public String getP_subtotal_taxed() {
        return p_subtotal_taxed;
    }

    public void setP_subtotal_taxed(String p_subtotal_taxed) {
        this.p_subtotal_taxed = p_subtotal_taxed;
    }

    public String getP_subtotal_exempt() {
        return p_subtotal_exempt;
    }

    public void setP_subtotal_exempt(String p_subtotal_exempt) {
        this.p_subtotal_exempt = p_subtotal_exempt;
    }

    public String getP_discount() {
        return p_discount;
    }

    public void setP_discount(String p_discount) {
        this.p_discount = p_discount;
    }

    public String getP_percent_discount() {
        return p_percent_discount;
    }

    public void setP_percent_discount(String p_percent_discount) {
        this.p_percent_discount = p_percent_discount;
    }

    public String getP_tax() {
        return p_tax;
    }

    public void setP_tax(String p_tax) {
        this.p_tax = p_tax;
    }

    public String getP_total() {
        return p_total;
    }

    public void setP_total(String p_total) {
        this.p_total = p_total;
    }

    public String getP_changing() {
        return p_changing;
    }

    public void setP_changing(String p_changing) {
        this.p_changing = p_changing;
    }

    public String getP_note() {
        return p_note;
    }

    public void setP_note(String p_note) {
        this.p_note = p_note;
    }

    public String getP_canceled() {
        return p_canceled;
    }

    public void setP_canceled(String p_canceled) {
        this.p_canceled = p_canceled;
    }

    public String getP_paid_up() {
        return p_paid_up;
    }

    public void setP_paid_up(String p_paid_up) {
        this.p_paid_up = p_paid_up;
    }

    public String getP_paid() {
        return p_paid;
    }

    public void setP_paid(String p_paid) {
        this.p_paid = p_paid;
    }

    public String getP_created_at() {
        return p_created_at;
    }

    public void setP_created_at(String p_created_at) {
        this.p_created_at = p_created_at;
    }

    public String getP_user_id() {
        return p_user_id;
    }

    public void setP_user_id(String p_user_id) {
        this.p_user_id = p_user_id;
    }

    public String getP_user_id_applied() {
        return p_user_id_applied;
    }

    public void setP_user_id_applied(String p_user_id_applied) {
        this.p_user_id_applied = p_user_id_applied;
    }

    public String getP_invoice_type_id() {
        return p_invoice_type_id;
    }

    public void setP_invoice_type_id(String p_invoice_type_id) {
        this.p_invoice_type_id = p_invoice_type_id;
    }

    public String getP_payment_method_id() {
        return p_payment_method_id;
    }

    public void setP_payment_method_id(String p_payment_method_id) {
        this.p_payment_method_id = p_payment_method_id;
    }

    public sale getP_sale() {
        return p_sale;
    }

    public void setP_sale(sale p_sale) {
        this.p_sale = p_sale;
    }

    public RealmList<Pivot> getP_productofacturas() {
        return p_productofacturas;
    }

    public void setP_productofacturas(RealmList<Pivot> p_productofacturas) {
        this.p_productofacturas = p_productofacturas;
    }

    public int getFacturaDePreventa() {
        return facturaDePreventa;
    }

    public void setFacturaDePreventa(int facturaDePreventa) {
        this.facturaDePreventa = facturaDePreventa;
    }

    @Override
    public String toString() {
        return "InvoiceDetallePreventa{" +
            "p_id=" + p_id +
            ", p_branch_office_id='" + p_branch_office_id + '\'' +
            ", p_numeration='" + p_numeration + '\'' +
            ", p_longitud=" + p_longitud +
            ", p_latitud=" + p_latitud +
            ", p_date='" + p_date + '\'' +
            ", p_times='" + p_times + '\'' +
            ", p_date_presale='" + p_date_presale + '\'' +
            ", p_time_presale='" + p_time_presale + '\'' +
            ", p_due_date='" + p_due_date + '\'' +
            ", p_subtotal='" + p_subtotal + '\'' +
            ", p_subtotal_taxed='" + p_subtotal_taxed + '\'' +
            ", p_subtotal_exempt='" + p_subtotal_exempt + '\'' +
            ", p_discount='" + p_discount + '\'' +
            ", p_percent_discount='" + p_percent_discount + '\'' +
            ", p_tax='" + p_tax + '\'' +
            ", p_total='" + p_total + '\'' +
            ", p_changing='" + p_changing + '\'' +
            ", p_note='" + p_note + '\'' +
            ", p_canceled='" + p_canceled + '\'' +
            ", p_paid_up='" + p_paid_up + '\'' +
            ", p_paid='" + p_paid + '\'' +
            ", p_created_at='" + p_created_at + '\'' +
            ", p_user_id='" + p_user_id + '\'' +
            ", p_user_id_applied='" + p_user_id_applied + '\'' +
            ", p_invoice_type_id='" + p_invoice_type_id + '\'' +
            ", p_payment_method_id='" + p_payment_method_id + '\'' +
            ", p_sale=" + p_sale +
            ", p_productofacturas=" + p_productofacturas +
                ",facturaDePreventa=" + facturaDePreventa +
            '}';
    }
}
