package org.example;

import java.rmi.RemoteException;
import java.util.List;
import java.util.PriorityQueue;

public class NodeReceiver implements NodeInterface {
    private Node myNode = null;

    public NodeReceiver(Node myNode) {
        this.myNode = myNode;
    }

    @Override
    public void receiveRequest(Address myAddress, Request request) throws RemoteException {
        System.out.println("================================ Эврика !!! =============================================");

        System.out.println(myAddress + "_______ is do Request: " + request);
        System.out.println("This is my node clocks: " + myNode.getLogicalClock());
        myNode.setLogicalClock(myNode.getLogicalClock()+1);
        System.out.println("This is my node clocks: " + myNode.getLogicalClock());



//
//        // Обновляем логические часы на основе временной метки в запросе
        myNode.setLogicalClock(Math.max(myNode.getLogicalClock(), request.getTimestamp()) + 1);
//
        PriorityQueue<Request>  requestQueue = myNode.getRequestQueue();
        System.out.println("This is my requestQueue " + myNode.getRequestQueue());

        requestQueue.add(request);

        System.out.println("This is my requestQueue after adding: " + myNode.getRequestQueue());

//        // Добавляем запрос в очередь запросов
         myNode.setRequestQueue(requestQueue);

        System.out.println("This is my requestQueue after adding: " + myNode.getRequestQueue());

//
//        // Отправляем ответ (REPLY) обратно отправителю запроса
        myNode.sendReply(request.getAddress());

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
}
