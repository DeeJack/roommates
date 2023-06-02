package it.unitn.disi.fumiprovv.roommates.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Expense implements Serializable {
    private String name;
    private double amount;
    private String payerId;
    private Date date;

    public Expense(String name, double amount, String payerId, Date date) {
        this.name = name;
        this.amount = amount;
        this.payerId = payerId;
        this.date = date;
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

    public String getDataString(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String res = day + "/" + month + "/" + year;
        return res;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
