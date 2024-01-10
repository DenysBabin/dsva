package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public class NodeReceiver implements NodeInterface {
    private Node myNode = null;

    public NodeReceiver(Node myNode) {
        this.myNode = myNode;
    }

    @Override
    public void receiveRequest(Address myAddress, Request request) throws RemoteException {
        System.out.println("Эврика !!!");
    }
}
