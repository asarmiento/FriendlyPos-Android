package com.friendlypos.reenvio_email.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailResponse {

    @SerializedName("customer")
    private customer customer;
    @SerializedName("invoices")
    private List<invoices> facturas;

    public EmailResponse(customer customer, List<invoices> facturas) {
        this.customer = customer;
        this.facturas = facturas;
    }

    public customer getCustomer() {
        return customer;
    }

    public void setCustomer(customer customer) {
        this.customer = customer;
    }

    public List<invoices> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<invoices> facturas) {
        this.facturas = facturas;
    }

    @Override
    public String toString() {
        return "EmailResponse{" +
                "customer='" + customer + '\'' +
                ", facturas=" + facturas +
                '}';
    }
}
