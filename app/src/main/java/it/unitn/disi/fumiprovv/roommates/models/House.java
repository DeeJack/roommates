package it.unitn.disi.fumiprovv.roommates.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class House {
    private final String id;
    private final String name;
    private final List<Note> notes = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<Roommate> roommates = new ArrayList<>();

    // Needed for Firestore
    public House() {
        id = "";
        name = "";
    }

    public House(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Roommate> getRoommates() {
        return roommates;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addRoommate(Roommate roommate) {
        roommates.add(roommate);
    }

    public static House createHouse(String name, String userId) {
        // Generate a unique id with 6 alphanumerical characters, check with firestore if it's already taken
        String id;
        boolean isTaken;

        do {
            id = generateRandomId();
            isTaken = isIdTaken(id);
        } while (isTaken);
        House house = new House(id, name);
        house.addRoommate(new Roommate(userId, Timestamp.now(), true));
        return house;
    }

    private static String generateRandomId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder idBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            idBuilder.append(randomChar);
        }

        return idBuilder.toString();
    }

    private static boolean isIdTaken(String id) {
        AtomicBoolean isTaken = new AtomicBoolean(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("case")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        isTaken.set(querySnapshot != null && !querySnapshot.isEmpty());
                    } else {
                        // Handle the error
                    }
                });

        return isTaken.get();
    }

}
