package org.example;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Comparable<Address>, Serializable {
    public String ip;
    public Integer port;


    public Address () {
        this("127.0.0.1", 2010);
    }


    public Address (String hostname, int port) {
        this.ip = hostname;
        this.port = port;
    }


    public Address (Address addr) {
        this(addr.ip, addr.port);
    }




    @Override
    public String toString() {
        return("Addr[host:'"+ip+"', port:'"+port+"']");
    }


    @Override
    public int compareTo(Address address) {
        int retval = 0;
        if ((retval = ip.compareTo(address.ip)) == 0 ) {
            if ((retval = port.compareTo(address.port)) == 0 ) {
                return 0;
            }
            else
                return retval;
        }
        else
            return retval;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(ip, address.ip) &&
                Objects.equals(port, address.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
