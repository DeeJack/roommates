package it.unitn.disi.fumiprovv.roommates.models;

import java.io.Serializable;

public class ShoppingItem implements Serializable {
    private String id;
    private String name;

    public ShoppingItem() {
    }

    public ShoppingItem(String id, String name) {
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

    public void setName(String name) {
        this.name = name;
    }
}
