package com.netwokz.flatratetracker;

/**
 * Created by Steve on 10/8/13.
 */
public class Ticket {

    private int ID;
    private long date;
    private double totalHours;
    private String make, model;

    public Ticket(int id, long date, String make, String model, double totalHours) {
        this.ID = id;
        this.date = date;
        this.make = make;
        this.model = model;
        this.totalHours = totalHours;
    }

    public Long getDate() {
        return date;
    }

    public int getID() {
        return ID;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }
}


