package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NodeInterface extends Remote {
    void receiveRequest(Address myAddress, Request request, int nodeLogicalClock) throws RemoteException;

    void proccessJoin(Address addr) throws RemoteException;

    List<Address> getKnownAddresses() throws RemoteException;

    void receiveReply(Request request, int nodeLogicalClock, Address nodeAddress) throws RemoteException;

    void receiveMessage(String msg, int lock, String name) throws RemoteException;
}
