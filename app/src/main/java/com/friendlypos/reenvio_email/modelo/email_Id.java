package com.friendlypos.reenvio_email.modelo;


public class email_Id {

    private String customer;

    public email_Id(String customer) {
        this.customer = customer;
    }

    public String getCostumer() {
        return customer;
    }

    public void setCostumer(String customer) {
        this.customer = customer;
    }



    @Override
    public String toString() {
        return "email_Id{" +
                "customer='" + customer + '\'' +
                '}';
    }
}
