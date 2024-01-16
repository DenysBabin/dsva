package org.example;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//ADDING NAME TO ARGS
public class Node implements Runnable {

    public final static String SERVICE_NAME = "SERVICE_NAME";
    public final static String MESSAGE_SERVICE_NAME = "MESSAGE_SERVICE_NAME";

    private String otherNodeIP = null;
    private int otherNodePort = -1;
    private int myPort = -1;
    private String name;
    private List<Address> pendingReplies = new ArrayList<>();
    private int logicalClock = 0;
    private PriorityQueue<Request> requestQueue = new PriorityQueue<>();
    boolean isHaveQueue;
    private ExecutorService executor = Executors.newCachedThreadPool();
    public String input = "";





    private List<Address> knownAddresses = new ArrayList<>();
//    private MessageInterface messageReceiver;
//    private NodeInterface nodeReceiver;

    public static void main(String[] args) {
        System.out.println("Start here");
        Node node = new Node(args, "name");
        node.run();
    }

    private Address myAddress;
    private CommunicationHub communicationHub;

    public Node(String[] args, String name) {
        this.name = name;
        this.isHaveQueue = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ip") && i + 1 < args.length) {
                otherNodeIP = args[++i];
            } else if (args[i].equals("-p") && i + 1 < args.length) {
                try {
                    otherNodePort = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else if (args[i].equals("-n") && i + 1 < args.length) {
                this.name = args[++i]; // Установка имени
            } else if (args[i].equals("-myPort") && i + 1 < args.length) {
                try {
                    myPort = Integer.parseInt(args[++i]); // Установка моего порта
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        if (myPort == -1) {
            throw new IllegalArgumentException("Port must be specified");
        }
    }

    @Override
    public void run() {
        try {
            String myIP = getMyIP();

            System.out.println(myIP);

            myAddress = new Address(myIP, myPort);
            communicationHub = new CommunicationHub(myAddress);

            startReceivers();

            if (Objects.nonNull(otherNodeIP) && Objects.nonNull(otherNodePort)) {
                try {
                    Address address = new Address(otherNodeIP, otherNodePort);
                    // messageReceiver
                    NodeInterface tmpNode = communicationHub.getRMIProxy(address);
                    System.out.println(tmpNode);
                    knownAddresses = tmpNode.getKnownAddresses();
                    knownAddresses.add(address);
                    join();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                this.input = input;

                if (input.equals("exit")) {
                    System.out.println("Exiting...");
                    break; // Выход из цикла и завершение программы
                } else {
                    // Критическая ситуация
                    sendCriticalSectionRequest();
                    this.input = "";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doRequest() throws RemoteException {
//        System.out.println("this.getRequestQueue() : " + this.getRequestQueue());
        Request firsRequest = this.getRequestQueue().peek();

        assert firsRequest != null;
        for (Address nodeAddress : knownAddresses) {
            if (!nodeAddress.equals(myAddress)) {
                pendingReplies.add(nodeAddress);
            }
        }


        if (!firsRequest.isStatus() && knownAddresses.isEmpty()) {
            System.out.println("You send message " + this.input);
            this.requestQueue.poll();
        }

        for (Address nodeAddress : knownAddresses) {
            if (!nodeAddress.equals(myAddress)) {
                NodeInterface tmpNode = communicationHub.getRMIProxy(nodeAddress);



                if (firsRequest.getType().equals("REQUEST") && !firsRequest.isStatus()) {
                    try {
                        this.setLogicalClock(this.getLogicalClock() + 1);

                        System.out.println("< ============= Request with Type REQUEST started ============= > Time: " + this.getLogicalClock());

                        tmpNode.receiveRequest(myAddress, firsRequest, this.getLogicalClock());

                    } catch (RemoteException e) {
                        System.err.println("RemoteException occurred: " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("General Exception occurred: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (firsRequest.getType().equals("REPLY") && !firsRequest.isStatus() ) {
                    this.setLogicalClock(this.getLogicalClock() + 1);
                    System.out.println("< ============= Request with Type REPLY started ============= > Time: " + this.getLogicalClock());
                    firsRequest.setReceiveLamportClock(this.getLogicalClock());

                    this.sendReply(firsRequest);
                } else if (firsRequest.getType().equals("RELEASE") && !firsRequest.isStatus()) {
                    this.setLogicalClock(this.getLogicalClock() + 1);
                    System.out.println("< ============= Request with Type RELEASE started ============= > Time: " + this.getLogicalClock());

                    this.sendMessage(nodeAddress);
                }
            }

        }
        firsRequest.setStatus(true);
        isHaveQueue = this.getRequestQueue().isEmpty();

        if (!isHaveQueue && !firsRequest.isStatus() && knownAddresses.size() >= 2) {
            doRequest();
        }
    }


    private void sendCriticalSectionRequest() throws RemoteException {
        System.out.println("======= Start msg =====");
        Request request = new Request(getMyAddress(), "REQUEST", false, this.getLogicalClock(), this.getLogicalClock());
        requestQueue.add(request);

        if (!this.requestQueue.isEmpty()) {
            doRequest();
        }

    }

    private void join() {
        System.out.println("Starting sending requests to other users");
        for (Address nodeAddress : knownAddresses) {
            try {
                NodeInterface tmpNode = communicationHub.getRMIProxy(nodeAddress);
                tmpNode.proccessJoin(myAddress);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private NodeInterface startReceivers() {
        System.setProperty("java.rmi.server.hostname", myAddress.ip);

        NodeInterface nodeReceiver = null;
        try {
            nodeReceiver = new NodeReceiver(this);
            NodeInterface nodeSkeleton = (NodeInterface) UnicastRemoteObject.exportObject(nodeReceiver, 40000+myAddress.port);
            Registry registry = LocateRegistry.createRegistry(myAddress.port);
            registry.rebind(SERVICE_NAME, nodeSkeleton);
        } catch (Exception e) {
            System.err.println("Message listener - something is wrong: " + e.getMessage());
        }
        System.out.println("Message listener is started ...");

        return nodeReceiver;
    }

    private static String getMyIP() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        String myIP = null;

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address && address.getHostAddress().startsWith("192.168")) {
                    myIP = address.getHostAddress();
                    break;
                }
            }

            if (myIP != null) break;
        }

        if (myIP == null) throw new RuntimeException("Не удалось найти локальный IP-адрес, начинающийся на 192.168");
        return myIP;
    }



    public List<Address> getKnownAddresses() {
        return this.knownAddresses;
    }

    public void setKnownAddresses(List<Address> knownAddresses) {
        this.knownAddresses = knownAddresses;
    }

    public Address getMyAddress() {
        return myAddress;
    }


    public int getLogicalClock() {
        return logicalClock;
    }

    public void setLogicalClock(int logicalClock) {
        this.logicalClock = logicalClock;
    }

    public PriorityQueue<Request> getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(PriorityQueue<Request> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void sendReply(Request request) {
//        this.setLogicalClock(this.getLogicalClock()+1);
        System.out.println("<==================== SENDING REPLY ====================> Time: " + this.getLogicalClock());
        System.out.println("User: " + name + " is sending a reply to " + request.getAddress());

        try {
            NodeInterface node = communicationHub.getRMIProxy(request.getAddress());
            node.receiveReply(request, this.getLogicalClock(), myAddress);

        } catch (RemoteException e) {
            System.out.println("Error sending reply to " + request.getAddress() + ": " + e.getMessage());
        }
    }

    public void sendMessage(Address address) throws RemoteException {
        System.out.println("User: " + name + " is send a MESSAGE to user in Address: " + address);
        NodeInterface node = communicationHub.getRMIProxy(address);
        node.receiveMessage(this.input, this.getLogicalClock(), this.name);
    }

    public List<Address> getPendingReplies() {
        return pendingReplies;
    }

    public void setPendingReplies(List<Address> pendingReplies) {
        this.pendingReplies = pendingReplies;
    }

    public boolean isHaveQueue() {
        return isHaveQueue;
    }

    public void setHaveQueue(boolean haveQueue) {
        isHaveQueue = haveQueue;
    }
}


