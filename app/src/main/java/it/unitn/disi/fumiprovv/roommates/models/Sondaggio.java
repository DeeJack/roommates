package it.unitn.disi.fumiprovv.roommates.models;

import android.util.Pair;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Sondaggio {
    private String domanda;
    private ArrayList<String> opzioni;
    private ArrayList<Long> voti;
    private Long tempoCreazione;
    private Boolean sceltaMultipla;
    private DocumentReference creatore;
    private ArrayList<String> votanti;
    private DocumentReference casa;
    private Long votiTotali;

    public Sondaggio(String domanda, ArrayList<String> opzioni, ArrayList<Long> voti, Long tempoCreazione, Boolean sceltaMultipla, DocumentReference creatore, DocumentReference casa) {
        this.domanda = domanda;
        this.opzioni = opzioni;
        this.voti = voti;
        this.tempoCreazione = tempoCreazione;
        this.sceltaMultipla = sceltaMultipla;
        this.creatore = creatore;
        this.votanti = new ArrayList<>();
        this.votiTotali = new Long(0);
        this.casa = casa;
    }

    public String getDomanda() {
        return domanda;
    }

    public void setDomanda(String domanda) {
        this.domanda = domanda;
    }

    public ArrayList<String> getOpzioni() {
        return opzioni;
    }

    public void setOpzioni(ArrayList<String> opzioni) {
        this.opzioni = opzioni;
    }

    public ArrayList<Long> getVoti() {
        return voti;
    }

    public void setVoti(ArrayList<Long> voti) {
        this.voti = voti;
    }

    public Long getTempoCreazione() {
        return tempoCreazione;
    }

    public void setTempoCreazione(Long tempoCreazione) {
        this.tempoCreazione = tempoCreazione;
    }

    public Boolean getSceltaMultipla() {
        return sceltaMultipla;
    }

    public void setSceltaMultipla(Boolean sceltaMultipla) {
        this.sceltaMultipla = sceltaMultipla;
    }

    public DocumentReference getCreatore() {
        return creatore;
    }

    public void setCreatore(DocumentReference creatore) {
        this.creatore = creatore;
    }

    public ArrayList<String> getVotanti() {
        return votanti;
    }

    public void setVotanti(ArrayList<String> votanti) {
        this.votanti = votanti;
    }

    public void addVotante(String v) {
        votanti.add(v);
    }
}
