package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.Timestamp;

public class Roommate {
    private final String userId;
    private final Timestamp joinDate;
    private boolean isModerator;

    public Roommate(String name, Timestamp joinDate, boolean isModerator) {
        this.userId = name;
        this.joinDate = joinDate;
        this.isModerator = isModerator;
    }

    // Needed for Firestore
    public Roommate() {
        userId = "";
        joinDate = null;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public boolean isModerator() {
        return isModerator;
    }
}
