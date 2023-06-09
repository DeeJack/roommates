package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class Pagamento {
    private String casa;
    private String from;
    private String to;
    private Double amount;
    private com.google.firebase.Timestamp date;
    private String userNameTo;
    private String userNameFrom;

    public Pagamento(String casa, String from, String to, Double amount, com.google.firebase.Timestamp date) {
        this.casa = casa;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUserNameTo() {
        return userNameTo;
    }

    public void setUserNameTo(String userNameTo) {
        this.userNameTo = userNameTo;
    }

    public String getUserNameFrom() {
        return userNameFrom;
    }

    public void setUserNameFrom(String userNameFrom) {
        this.userNameFrom = userNameFrom;
    }
}
