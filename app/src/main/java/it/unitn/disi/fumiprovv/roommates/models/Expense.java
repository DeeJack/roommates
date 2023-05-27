package it.unitn.disi.fumiprovv.roommates.models;

import java.io.Serializable;

public class Expense implements Serializable {
    private String name;
    private double amount;
    private String payerId;

    public Expense(String name, double amount, String payerId) {
        this.name = name;
        this.amount = amount;
        this.payerId = payerId;
    }

    public Expense() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
}
