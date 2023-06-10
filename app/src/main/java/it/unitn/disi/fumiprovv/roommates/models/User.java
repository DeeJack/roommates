package it.unitn.disi.fumiprovv.roommates.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private final String name;

    public User(String name) {
        this.name = name;
    }

    // Needed for Firestore
    public User() {
        name = "";
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
