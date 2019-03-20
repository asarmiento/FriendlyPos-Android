package com.friendlypos.reenvio_email.modelo;

public class send_email_id {

    private String invoice;

    public send_email_id(String invoice) {
        this.invoice = invoice;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "send_email_id{" +
                "invoice='" + invoice + '\'' +
                '}';
    }
}
