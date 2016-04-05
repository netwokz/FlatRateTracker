package com.netwokz.flatratetracker;

/**
 * Created by Steve on 10/8/13.
 */
public class Vehicle {

    private static Vehicle INSTANCE = null;

    int year;
    String make;
    String model;

    public static Vehicle getInstance(int vehicleYear, String vehicleMake, String vehicleModel) {
        if (INSTANCE == null) {
            INSTANCE = new Vehicle(vehicleYear, vehicleMake, vehicleModel);
        }
        return INSTANCE;
    }

    public Vehicle(int vehicleYear, String vehicleMake, String vehicleModel) {
        year = vehicleYear;
        make = vehicleMake;
        model = vehicleModel;
    }
}
