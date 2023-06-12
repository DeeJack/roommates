package it.unitn.disi.fumiprovv.roommates.models;

public class SpesaComune {
    private String id;
    private String casa;
    private String nome;
    private Double valore;
    private String responsabile;
    private String ripetizione;
    private Long lastWeek;
    private Long lastMonth;
    private Long lastYear;
    private String userName;

    public SpesaComune(String id, String casa, String nome, Double valore, String responsabile, String ripetizione) {
        this.id = id;
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

    public Double getValore() {
        return valore;
    }

    public void setValore(Double valore) {
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

    public Long getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(Long lastWeek) {
        this.lastWeek = lastWeek;
    }

    public Long getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(Long lastMonth) {
        this.lastMonth = lastMonth;
    }

    public Long getLastYear() {
        return lastYear;
    }

    public void setLastYear(Long lastYear) {
        this.lastYear = lastYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserName(String nome) {
        this.userName = nome;
    }

    public String getUserName() {
        return this.userName;
    }
}
