package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.Timestamp;

public class Note {
    private String id;
    private String userId;
    private String userName;
    private Timestamp creationDate;
    private String text;

    // Needed for Firestore
    public Note() {
    }

    public Note(String id, String userId, String userName, Timestamp creationDate, String text) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.creationDate = creationDate;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }
}
