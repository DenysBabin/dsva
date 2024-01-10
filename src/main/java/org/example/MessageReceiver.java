package org.example;

import java.rmi.RemoteException;
import java.util.List;

public class MessageReceiver implements MessageInterface {
    private Node myNode = null;

    public MessageReceiver(Node myNode) {
        this.myNode = myNode;
    }

    @Override
    public void proccessJoin(Address addr) throws RemoteException {
        System.out.println("JOIN request was called from IP " + addr.toString());
        if (addr.compareTo(myNode.getMyAddress()) == 0) {
            System.out.println("It's my IP, comething went grond, but it'snot critical thing");
        } else {
            System.out.println("Some user join chat!");
            List<Address> tempKnownAddresses = myNode.getKnownAddresses();
            tempKnownAddresses.add(addr);
            myNode.setKnownAddresses(tempKnownAddresses);
            System.out.println("Adding new user was successfully completed!");
        }
    }

    @Override
    public List<Address> getAllUserAddresses() throws RemoteException {
        return  myNode.getKnownAddresses();
    }

    @Override
    public List<Address> getKnownAddresses() throws RemoteException {
        return myNode.getKnownAddresses();
    }
}
