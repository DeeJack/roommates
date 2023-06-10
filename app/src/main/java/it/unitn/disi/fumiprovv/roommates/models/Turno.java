package it.unitn.disi.fumiprovv.roommates.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Turno {
    private String id;
    private String name;
    private Long weekStart;
    private Long yearStart;
    private ArrayList<String> users;
    private String house;
    private Long yearLast;
    private Long weekLast;
    private ArrayList<String> userNames;
    private Map<String, String> userNames2;

    public Turno(String name, Long weekInizio, Long yearStart, ArrayList<String> users, String house, Long yearLast, Long weekLast) {
        this.name = name;
        this.weekStart = weekInizio;
        this.yearStart = yearStart;
        this.users = users;
        this.house = house;
        this.yearLast = yearLast;
        this.weekLast = weekLast;
        userNames = new ArrayList<>();
        userNames2 = new HashMap<>();
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

    public Long getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(Long weekStart) {
        this.weekStart = weekStart;
    }

    public Long getYearStart() {
        return yearStart;
    }

    public void setYearStart(Long yearStart) {
        this.yearStart = yearStart;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public Long getYearLast() {
        return yearLast;
    }

    public void setYearLast(Long yearLast) {
        this.yearLast = yearLast;
    }

    public Long getWeekLast() {
        return weekLast;
    }

    public void setWeekLast(Long weekLast) {
        this.weekLast = weekLast;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", weekStart=" + weekStart +
                ", yearStart=" + yearStart +
                ", users=" + users +
                ", house='" + house + '\'' +
                ", yearLast=" + yearLast +
                ", weekLast=" + weekLast +
                '}';
    }

    public void addUserName(String name) {
        this.userNames.add(name);
    }

    public String getUserNames(){
        return userNames.toString();
    }
    public String getUserNames2(){
        return userNames2.toString();
    }

    public void addUserName(String id, String name) {
        userNames2.put(id,name);
    }

    public String getUserNameAt(String id) {
        return userNames2.get(id);
    }
}
