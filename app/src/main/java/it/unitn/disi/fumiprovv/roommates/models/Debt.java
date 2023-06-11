package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Debt {
    private String id;
    private String house;
    private String idFrom;
    private String idTo;
    private String userNameFrom;
    private String userNameTo;
    private Double amount;

    public Debt(String id, String house, String idFrom, String idTo, Double amount) {
        this.id = id;
        this.house = house;
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(String idFrom) {
        this.idFrom = idFrom;
    }

    public String getIdTo() {
        return idTo;
    }

    public void setIdTo(String idTo) {
        this.idTo = idTo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUserNameFrom() {
        return userNameFrom;
    }

    public void setUserNameFrom(String userNameFrom) {
        this.userNameFrom = userNameFrom;
    }

    public String getUserNameTo() {
        return userNameTo;
    }

    public void setUserNameTo(String userNameTo) {
        this.userNameTo = userNameTo;
    }

    @Override
    public String toString() {
        return "Debt{" +
                "id='" + id + '\'' +
                ", house='" + house + '\'' +
                ", idFrom='" + idFrom + '\'' +
                ", idTo='" + idTo + '\'' +
                ", amount=" + amount +
                '}';
    }
}
