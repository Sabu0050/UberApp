package com.sabututexp.uberapp.models;

/**
 * Created by s on 11/1/17.
 */

public class HistoryElements {
    private String rideId;
    private String time;

    public HistoryElements(String rideId, String time) {
        this.rideId = rideId;
        this.time = time;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
