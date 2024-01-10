package org.example;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class CommunicationHub {
    private final Address myAddress;

    public CommunicationHub(Address myAddress) {
            this.myAddress = myAddress;
    }

    public NodeInterface getRMIProxy(Address address) throws RemoteException {
        if (address.compareTo(myAddress) == 0 ) return null;
        else {
            try {
                Registry registry = LocateRegistry.getRegistry(address.ip, address.port);
                return (NodeInterface) registry.lookup(Node.SERVICE_NAME);
            } catch (NotBoundException nbe) {
                // transitive RM exception
                throw new RemoteException(nbe.getMessage());
            }
        }
    }

    public MessageInterface getMessageReceiverProxy(Address address) throws RemoteException {
        if (address.compareTo(myAddress) == 0 ) return null;
        else {
            try {
                Registry registry = LocateRegistry.getRegistry(address.ip, address.port);
                // Используйте уникальное имя для поиска MessageReceiver
                return (MessageInterface) registry.lookup(Node.MESSAGE_SERVICE_NAME);
            } catch (NotBoundException nbe) {
                throw new RemoteException(nbe.getMessage());
            }
        }
    }
}
