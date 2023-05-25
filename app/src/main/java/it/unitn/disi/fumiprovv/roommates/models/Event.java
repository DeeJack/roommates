package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Event {
    private DocumentReference casa;
    private String nome;
    private Long data;
    // Needed for Firestore
    public Event(DocumentReference c, String n, Long d) {
        casa = c;
        nome = n;
        data = d;
    }

    public Event(){
        casa = null;
        nome = "";
        data = new Long(0);
    }

    public String getNome() {
        return nome;
    }

    public Long getData() {
        return data;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public DocumentReference getCasa() {
        return casa;
    }

    public void setCasa(DocumentReference casa) {
        this.casa = casa;
    }
}
