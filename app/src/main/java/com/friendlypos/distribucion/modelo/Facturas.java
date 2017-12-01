package com.friendlypos.distribucion.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DelvoM on 02/11/2017.
 */

public class Facturas extends RealmObject {
//TODO REVISAR ESTAS KEYS
    @PrimaryKey
    private String id;

    private String branch_office_id;
    private String numeration;
    private String date;
    private String times;
    private String date_presale;
    private String time_presale;
    private String due_date;
    private String subtotal;
    private String subtotal_taxed;
    private String subtotal_exempt;
    private String discount;
    private String percent_discount;
    private String tax;
    private String total;
    private String changing;
    private String note;
    private String canceled;
    private String paid_up;
    private String paid;
    private String created_at;
    private String user_id;
    private String user_id_applied;
    private String invoice_type_id;
    private String payment_method_id;
    @SerializedName("sale")
    private Venta venta;
    @SerializedName("product_invoice")
    private RealmList<Pivot> productofacturas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch_office_id() {
        return branch_office_id;
    }

    public void setBranch_office_id(String branch_office_id) {
        this.branch_office_id = branch_office_id;
    }

    public String getNumeration() {
        return numeration;
    }

    public void setNumeration(String numeration) {
        this.numeration = numeration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getDate_presale() {
        return date_presale;
    }

    public void setDate_presale(String date_presale) {
        this.date_presale = date_presale;
    }

    public String getTime_presale() {
        return time_presale;
    }

    public void setTime_presale(String time_presale) {
        this.time_presale = time_presale;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getSubtotal_taxed() {
        return subtotal_taxed;
    }

    public void setSubtotal_taxed(String subtotal_taxed) {
        this.subtotal_taxed = subtotal_taxed;
    }

    public String getSubtotal_exempt() {
        return subtotal_exempt;
    }

    public void setSubtotal_exempt(String subtotal_exempt) {
        this.subtotal_exempt = subtotal_exempt;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPercent_discount() {
        return percent_discount;
    }

    public void setPercent_discount(String percent_discount) {
        this.percent_discount = percent_discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getChanging() {
        return changing;
    }

    public void setChanging(String changing) {
        this.changing = changing;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCanceled() {
        return canceled;
    }

    public void setCanceled(String canceled) {
        this.canceled = canceled;
    }

    public String getPaid_up() {
        return paid_up;
    }

    public void setPaid_up(String paid_up) {
        this.paid_up = paid_up;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id_applied() {
        return user_id_applied;
    }

    public void setUser_id_applied(String user_id_applied) {
        this.user_id_applied = user_id_applied;
    }

    public String getInvoice_type_id() {
        return invoice_type_id;
    }

    public void setInvoice_type_id(String invoice_type_id) {
        this.invoice_type_id = invoice_type_id;
    }

    public String getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(String payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

   public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public RealmList<Pivot> getProductofactura() {
        return productofacturas;
    }

    public void setProductofactura(RealmList<Pivot> productofacturas) {
        this.productofacturas = productofacturas;
    }

    public String toString() {
        return "Facturas{" +
                "id='" + id + '\'' +
                ", branch_office_id='" + branch_office_id + '\'' +
                ", numeration='" + numeration + '\'' +
                ", date='" + date + '\'' +
                ", times='" + times + '\'' +
                ", date_presale='" + date_presale + '\'' +
                ", time_presale='" + time_presale + '\'' +
                ", due_date='" + due_date + '\'' +
                ", subtotal='" + subtotal + '\'' +
                ", subtotal_taxed='" + subtotal_taxed + '\'' +
                ", subtotal_exempt='" + subtotal_exempt + '\'' +
                ", discount='" + discount + '\'' +
                ", percent_discount='" + percent_discount + '\'' +
                ", tax='" + tax + '\'' +
                ", total='" + total + '\'' +
                ", changing='" + changing + '\'' +
                ", note='" + note + '\'' +
                ", canceled='" + canceled + '\'' +
                ", paid_up='" + paid_up + '\'' +
                ", paid='" + paid + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_id_applied='" + user_id_applied + '\'' +
                ", invoice_type_id='" + invoice_type_id + '\'' +
                ", payment_method_id='" + payment_method_id + '\'' +
                ", venta=" + venta +
                ", productofacturas=" + productofacturas +
                '}';
    }
}
