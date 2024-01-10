package org.example;

import java.io.Serializable;

public class Request implements Serializable, Comparable<Request> {
    private final int timestamp;
    private final Address address;

    public Request(int timestamp, Address address) {
        this.timestamp = timestamp;
        this.address = address;
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
}


