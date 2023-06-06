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
    private String creatore;
    private ArrayList<String> votanti;
    private String casa;
    private Long votiTotali;
    private String id;
    private Long maxVotanti;

    public Sondaggio(String id, String domanda, ArrayList<String> opzioni, ArrayList<Long> voti, Long tempoCreazione, Boolean sceltaMultipla, String creatore, String casa, ArrayList<String> votanti, Long maxVotanti, Long votiTotali) {
        this.id = id;
        this.domanda = domanda;
        this.opzioni = new ArrayList<String>();
        this.opzioni.addAll(opzioni);
        this.voti = new ArrayList<Long>();
        this.voti.addAll(voti);
        this.tempoCreazione = tempoCreazione;
        this.sceltaMultipla = sceltaMultipla;
        this.creatore = creatore;
        this.votiTotali = votiTotali;
        this.casa = casa;
        this.votanti = new ArrayList<String>();
        this.votanti.addAll(votanti);
        this.maxVotanti = maxVotanti;
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

    public String getCreatore() {
        return creatore;
    }

    public void setCreatore(String creatore) {
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

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public Long getVotiTotali() {
        return votiTotali;
    }

    public void setVotiTotali(Long votiTotali) {
        this.votiTotali = votiTotali;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMaxVotanti() {
        return maxVotanti;
    }

    public void setMaxVotanti(Long maxVotanti) {
        this.maxVotanti = maxVotanti;
    }

    public String getVincitore(){
        Long massimo = new Long(0);
        String res = "";
        for(int i=0;i<voti.size();i++) {
            if(voti.get(i) > massimo) {
                massimo = voti.get(i);
                res = opzioni.get(i);
            }
        }
        return res;
    }

    public void addVoto(int i) {
        voti.set(i,voti.get(i)+1);
    }
}
