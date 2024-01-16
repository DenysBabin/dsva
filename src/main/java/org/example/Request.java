package org.example;

import java.io.Serializable;

public class Request implements Serializable, Comparable<Request> {
    private final int timestamp;
    private final Address address;
    private String type;
    boolean status;
    private int createdLamportClock;
    private int receiveLamportClock;


    public Request(Address address, String type, Boolean status, int createdLamportClock, int receiveLamportClock) {
        this.timestamp = 0;
        this.address = address;
        this.type = type;
        this.status = status;
        this.receiveLamportClock = receiveLamportClock;
        this.createdLamportClock = createdLamportClock;
    }

    public int getTimestamp() {
        return timestamp;
    }
    // TODO - возможно неправильно сделал
    @Override
    public int compareTo(Request other) {
        if (this.timestamp == other.timestamp) {
            return this.address.compareTo(other.address);
        }
        return this.timestamp - other.timestamp;
    }

    public Address getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCreatedLamportClock() {
        return createdLamportClock;
    }

    public void setCreatedLamportClock(int createdLamportClock) {
        this.createdLamportClock = createdLamportClock;
    }

    public int getReceiveLamportClock() {
        return receiveLamportClock;
    }

    public void setReceiveLamportClock(int receiveLamportClock) {
        this.receiveLamportClock = receiveLamportClock;
    }
}


