package it.unitn.disi.fumiprovv.roommates.models;

public class SpesaComune {
    private String casa;
    private String nome;
    private Long valore;
    private String responsabile;
    private String ripetizione;

    public SpesaComune(String casa, String nome, Long valore, String responsabile, String ripetizione) {
        this.casa = casa;
        this.nome = nome;
        this.valore = valore;
        this.responsabile = responsabile;
        this.ripetizione = ripetizione;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getValore() {
        return valore;
    }

    public void setValore(Long valore) {
        this.valore = valore;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String responsabile) {
        this.responsabile = responsabile;
    }

    public String getRipetizione() {
        return ripetizione;
    }

    public void setRipetizione(String ripetizione) {
        this.ripetizione = ripetizione;
    }
}
