package com.friendlypos.reenvio_email.modelo;


public class email_Id {

    private String costumer;

    public email_Id(String costumer) {
        this.costumer = costumer;
    }

    public String getCostumer() {
        return costumer;
    }

    public void setCostumer(String costumer) {
        this.costumer = costumer;
    }



    @Override
    public String toString() {
        return "email_Id{" +
                "costumer='" + costumer + '\'' +
                '}';
    }
}
