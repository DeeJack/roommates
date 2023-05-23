package it.unitn.disi.fumiprovv.roommates.models;

public class Event {
    private String nome;
    private String data;
    // Needed for Firestore
    public Event(String n, String d) {
        nome = n;
        data = d;
    }

    public Event(){
        nome = "";
        data = "";
    }
}
