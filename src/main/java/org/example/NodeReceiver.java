package org.example;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import static org.example.LoggingToFile.LOGGERFILE;

public class NodeReceiver implements NodeInterface {
    private Node myNode = null;

    public NodeReceiver(Node myNode) {
        this.myNode = myNode;
    }

    @Override
    public void receiveRequest(Address myAddress, Request request, int nodeLogicalClock) throws RemoteException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
        }

        myNode.setLogicalClock(Math.max(myNode.getLogicalClock(), nodeLogicalClock) + 1);
        System.out.println("< ================= Обработка Запроса ==============> Time: " + myNode.getLogicalClock());
        LOGGERFILE.info("< ================= Обработка Запроса ==============> Time: " + myNode.getLogicalClock());

        PriorityQueue<Request> requestQueue = myNode.getRequestQueue();
        Request newRequest = new Request(myAddress, "REPLY", false, request.getCreatedLamportClock(), myNode.getLogicalClock());
        requestQueue.add(newRequest);
        LOGGERFILE.info("myNode: " + myNode.getMyAddress() + " has requestQueue: " + myNode.getRequestQueue());

        if ( !myNode.getRequestQueue().isEmpty()) {
            myNode.doRequest(newRequest);
        }
    }

    @Override
    public void proccessJoin(Address addr) throws RemoteException {
        LOGGERFILE.info("JOIN request was called from IP " + addr.toString());
        if (addr.compareTo(myNode.getMyAddress()) == 0) {
            LOGGERFILE.info("It's my IP, comething went grond, but it'snot critical thing");
        } else {
            LOGGERFILE.info("Some user join chat!");
            System.out.println("Some user join chat!");

            List<Address> tempKnownAddresses = myNode.getKnownAddresses();
            tempKnownAddresses.add(addr);
            myNode.setKnownAddresses(tempKnownAddresses);
            System.out.println("Adding new user was successfully completed!");
            LOGGERFILE.info("Adding new user was successfully completed!");
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
        LOGGERFILE.info("<===================== RECEIVE REPLY ===================> Time: " + myNode.getLogicalClock());
        LOGGERFILE.info("receiveReply start for request " + request);
        List<Address> pendingReplies = myNode.pendingRepliesMap.get(request.getCreatedLamportClock());
        LOGGERFILE.info("getCreatedLamportClock" + request.getCreatedLamportClock());

        LOGGERFILE.info("PENDING REPLIES: " + pendingReplies);
        pendingReplies.removeIf(address -> Objects.equals(nodeAddress.getPort(), address.getPort())
                && Objects.equals(nodeAddress.getIp(), address.getIp())
                && request.getCreatedLamportClock() <= Math.max(request.getCreatedLamportClock(), nodeLogicalClock));


        LOGGERFILE.info("REQUESTS QUEUE: " + myNode.getRequestQueue());

        if (pendingReplies.isEmpty() && !myNode.getRequestQueue().isEmpty()) {

            myNode.getRequestQueue().poll();
            myNode.setRequestQueue(myNode.getRequestQueue());

            Request newRequest = new Request(request.getAddress(), "RELEASE", false, request.getCreatedLamportClock(), request.getReceiveLamportClock());
            myNode.getRequestQueue().add(newRequest);
            myNode.setRequestQueue(myNode.getRequestQueue());
            myNode.setHaveQueue(false);
            if (!myNode.getRequestQueue().isEmpty()) {
                myNode.doRequest(newRequest);
            }
        }
    }

    @Override
    public void receiveMessage(String msg, int lock, String name) throws RemoteException {
        LOGGERFILE.info("User: " + name + " sent Message: " + msg + " Time: " + lock);
        System.out.println("User: " + name + " sent Message: " + msg + " Time: " + lock);
        myNode.getRequestQueue().poll();
    }

    @Override
    public void processExit(Request request, int logicalClock) {
        myNode.handleExitNotification(request, logicalClock);
    }

    @Override
    public void deleteFormPendingsList(Address nodeAddress) {
        for (List<Address> pendingReplies : myNode.pendingRepliesMap.values()) {
            pendingReplies.removeIf(address -> Objects.equals(nodeAddress.getPort(), address.getPort())
                    && Objects.equals(nodeAddress.getIp(), address.getIp()));
        }
    }
}
