package it.unitn.disi.fumiprovv.roommates.models;

import android.util.Pair;

import java.util.ArrayList;

public class Sondaggio {
    private String domanda;
    private ArrayList<String> opzioni;
    private ArrayList<Long> voti;
    private Long tempoCreazione;
    private Boolean sceltaMultipla;

    public Sondaggio(String domanda, ArrayList<String> opzioni, ArrayList<Long> voti, Long tempoCreazione, Boolean sceltaMultipla) {
        this.domanda = domanda;
        this.opzioni = opzioni;
        this.voti = voti;
        this.tempoCreazione = tempoCreazione;
        this.sceltaMultipla = sceltaMultipla;
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

    public boolean isSceltaMultipla(){
        if (sceltaMultipla == Boolean.TRUE) {
            return true;
        }
        return false;
    }
}
