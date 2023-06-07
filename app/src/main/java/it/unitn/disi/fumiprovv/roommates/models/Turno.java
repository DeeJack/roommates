package it.unitn.disi.fumiprovv.roommates.models;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;

public class Turno {
    private String id;
    private String luogo;
    private Long weekInizio;
    private Long annoInizio;
    private ArrayList<String> utenti;

    public Turno(String id, String luogo, Long weekInizio, Long annoInizio, ArrayList<String> utenti) {
        this.id = id;
        this.luogo = luogo;
        this.weekInizio = weekInizio;
        this.annoInizio = annoInizio;
        this.utenti = utenti;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public Long getWeekInizio() {
        return weekInizio;
    }

    public void setWeekInizio(Long weekInizio) {
        this.weekInizio = weekInizio;
    }

    public Long getAnnoInizio() {
        return annoInizio;
    }

    public void setAnnoInizio(Long annoInizio) {
        this.annoInizio = annoInizio;
    }

    public ArrayList<String> getUtenti() {
        return utenti;
    }

    public void setUtenti(ArrayList<String> utenti) {
        this.utenti = utenti;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
