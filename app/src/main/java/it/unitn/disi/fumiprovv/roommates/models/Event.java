package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Event {
    private DocumentReference casa;
    private String nome;
    private Long giorno;
    private Long mese;
    private Long anno;
    // Needed for Firestore
    public Event(DocumentReference c, String n, Long g, Long m, Long a) {
        casa = c;
        nome = n;
        giorno = g;
        mese = m;
        anno = a;
    }

    public Event(){
        casa = null;
        nome = "";
        giorno = new Long(1);
        mese = new Long(1);
        anno = new Long(2023);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public DocumentReference getCasa() {
        return casa;
    }

    public void setCasa(DocumentReference casa) {
        this.casa = casa;
    }

    public Long getGiorno() {
        return giorno;
    }

    public void setGiorno(Long giorno) {
        this.giorno = giorno;
    }

    public Long getMese() {
        return mese;
    }

    public void setMese(Long mese) {
        this.mese = mese;
    }

    public Long getAnno() {
        return anno;
    }

    public void setAnno(Long anno) {
        this.anno = anno;
    }
}
