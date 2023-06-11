package it.unitn.disi.fumiprovv.roommates.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Expense implements Serializable {
    private String name;
    private double amount;
    private String payerId;
    private Date date;
    private ArrayList<DocumentReference> paganti;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userNamePayer;
    private List<String> userNames;

    public Expense(String name, double amount, String payerId, Date date, ArrayList<DocumentReference> paganti) {
        this.name = name;
        this.amount = amount;
        this.payerId = payerId;
        this.date = date;
        this.paganti = paganti;
        this.userNames = new ArrayList<>();
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

    public String getPagantiString(){
        String s = "Diviso tra: ";
        for(int i=0;i<paganti.size()-1;i++) {
            s+= paganti.get(i).toString() + ", ";
        }
        s += paganti.get(paganti.size()-1).toString();
        return s;
    }

    public ArrayList<DocumentReference> getPaganti() {
        return paganti;
    }

    public void setPaganti(ArrayList<DocumentReference> paganti) {
        this.paganti = paganti;
    }

    public void setUserNamePayer(String nome) {
        this.userNamePayer = nome;
    }

    public String getUserNamePayer() {
        return this.userNamePayer;
    }

    public void addUserName(String fieldValue) {
        this.userNames.add(fieldValue);
    }

    public List<String> getUserNames(){
        return this.userNames;
    }
}
