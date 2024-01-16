package org.example;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class NodeReceiver implements NodeInterface {
    private Node myNode = null;

    public NodeReceiver(Node myNode) {
        this.myNode = myNode;
    }

    @Override
    public void receiveRequest(Address myAddress, Request request, int nodeLogicalClock) throws RemoteException {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException ie) {
//            Thread.currentThread().interrupt(); // Восстановление прерванного статуса
//            return; // Опционально: Выход из метода, если поток был прерван
//        }
        myNode.setLogicalClock(Math.max(myNode.getLogicalClock(), nodeLogicalClock) + 1);

        System.out.println("< ================= Обработка Запроса ==============> Time: " + myNode.getLogicalClock());

        PriorityQueue<Request> requestQueue = myNode.getRequestQueue();
        Request newRequest = new Request(myAddress, "REPLY", false, myNode.getLogicalClock(), myNode.getLogicalClock());
        requestQueue.add(newRequest);
        System.out.println("myNode: " + myNode.getMyAddress() + " has requestQueue: " + myNode.getRequestQueue());

        if ( !myNode.getRequestQueue().isEmpty()) {
            myNode.doRequest();
        }
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
    public List<Address> getKnownAddresses() throws RemoteException {
        return myNode.getKnownAddresses();
    }

    @Override
    public void receiveReply(Request request, int nodeLogicalClock, Address nodeAddress) throws RemoteException {

        myNode.setLogicalClock(Math.max(myNode.getLogicalClock(), nodeLogicalClock) + 1);

        System.out.println("<===================== RECEIVE REPLY ===================> Time: " + myNode.getLogicalClock());
        System.out.println("receiveReply start for request " + request);

//        System.out.println("check Time:");
//        System.out.println("request.getCreatedLamportClock(): Time: " + request.getCreatedLamportClock());
//        System.out.println("request.getCreatedLamportClock(): Time: " + request.getCreatedLamportClock());
//        System.out.println("nodeLogicalClock: Time: " + nodeLogicalClock);

//        System.out.println("My pending before remove is: " + myNode.getPendingReplies());

//        System.out.println("Data:  getPort: " + nodeAddress.getPort() + " myNode.getMyAddress().getIp(): " + nodeAddress.getIp() + " Math.max(myNode.getLogicalClock(), nodeLogicalClock) " + Math.max(request.getCreatedLamportClock(), nodeLogicalClock) + "  myNode.getLogicalClock(): " +  myNode.getLogicalClock() );


        myNode.getPendingReplies().removeIf(address -> Objects.equals(nodeAddress.getPort(), address.getPort())
                && Objects.equals(nodeAddress.getIp(), address.getIp())
                && request.getCreatedLamportClock() <= Math.max(request.getCreatedLamportClock(), nodeLogicalClock));


        System.out.println("PENDING REPLIES: " + myNode.getPendingReplies());
        System.out.println("REQUESTS QUEUE: " + myNode.getRequestQueue());

        if (myNode.getPendingReplies().isEmpty() && !myNode.getRequestQueue().isEmpty()) {

            myNode.getRequestQueue().poll();
            myNode.setRequestQueue(myNode.getRequestQueue());

            Request newRequest = new Request(request.getAddress(), "RELEASE", false, request.getCreatedLamportClock(), request.getReceiveLamportClock());
            myNode.getRequestQueue().add(newRequest);
            myNode.setRequestQueue(myNode.getRequestQueue());
            myNode.setHaveQueue(false);
            if (!myNode.getRequestQueue().isEmpty()) {
                myNode.doRequest();
            }
        } else if (!myNode.getPendingReplies().isEmpty()) {
            myNode.getPendingReplies().remove(0);
        }

    }

    @Override
    public void receiveMessage(String msg, int lock, String name) throws RemoteException {
        System.out.println("User: " + name + " sent Message: " + msg + " Time: " + lock);
        myNode.getRequestQueue().poll();
    }
}
