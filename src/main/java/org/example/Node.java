package org.example;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

//ADDING NAME TO ARGS
public class Node implements Runnable {

    public final static String SERVICE_NAME = "SERVICE_NAME";
    public final static String MESSAGE_SERVICE_NAME = "MESSAGE_SERVICE_NAME";

    private String otherNodeIP = null;
    private int otherNodePort = -1;
    private int myPort = -1;
    private String name;

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
//            messageReceiver = startMessageReceiver();
//            nodeReceiver = startNodeRMI();

            startReceivers();

            if (Objects.nonNull(otherNodeIP) && Objects.nonNull(otherNodePort)) {
                try {
                    Address address = new Address(otherNodeIP, otherNodePort);
                    MessageInterface tmpNode = communicationHub.getMessageReceiverProxy(address);
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
    }

    private void join() {
        System.out.println("Starting sending requests to other users");
        for (Address nodeAddress : knownAddresses) {
            try {
                MessageInterface tmpNode = communicationHub.getMessageReceiverProxy(nodeAddress);
                tmpNode.proccessJoin(myAddress);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private MessageInterface startReceivers() {
        System.setProperty("java.rmi.server.hostname", myAddress.ip);

        MessageInterface msgReceiver = null;
        NodeInterface nodeReceiver = null;


        try {
            msgReceiver = new MessageReceiver(this);
            nodeReceiver = new NodeReceiver(this);


            MessageInterface msgSkeleton = (MessageInterface) UnicastRemoteObject.exportObject(msgReceiver, 40000+myAddress.port);
            NodeInterface nodeSkeleton = (NodeInterface) UnicastRemoteObject.exportObject(nodeReceiver, 30000+myAddress.port);


            Registry registry = LocateRegistry.createRegistry(myAddress.port);

            registry.rebind(SERVICE_NAME, nodeSkeleton);
            registry.rebind(MESSAGE_SERVICE_NAME, msgSkeleton);

        } catch (Exception e) {
            System.err.println("Message listener - something is wrong: " + e.getMessage());
        }
        System.out.println("Message listener is started ...");

        return msgReceiver;
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
}


