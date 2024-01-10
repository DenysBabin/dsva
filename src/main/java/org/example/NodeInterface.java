package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NodeInterface extends Remote {
    void receiveRequest(Address myAddress, Request request) throws RemoteException;

    void proccessJoin(Address addr) throws RemoteException;

    List<Address> getKnownAddresses() throws RemoteException;
}
