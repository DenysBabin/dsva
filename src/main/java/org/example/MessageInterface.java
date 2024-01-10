package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MessageInterface extends Remote {
    void proccessJoin(Address addr) throws RemoteException;

    List<Address> getAllUserAddresses() throws RemoteException;

    List<Address> getKnownAddresses() throws RemoteException;
}
