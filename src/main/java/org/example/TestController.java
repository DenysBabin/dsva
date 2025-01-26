package org.example;

import org.springframework.web.bind.annotation.*;

import java.rmi.RemoteException;

@RestController
@RequestMapping("/api/node")
public class TestController {
    private Node node; // Экземпляр узла

//    public TestController() {
//        // Инициализируем узел (здесь пример с портом 8082, можно динамически менять)
//        String[] args = {"-ip", "localhost", "-p", "1999", "-myPort", "8082", "-n", "MyNode"};
//        this.node = new Node(args, "MyNode");
//        new Thread(node::run).start();
//    }

    @GetMapping("/test")
    public String test() {
        System.out.println("Test endpoint reached"); // Лог в консоль
        return "Server is running!";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam String message) {
        try {
            System.out.println(node);
            System.out.println("Received API_TO_SEND_message: " + message); // Логируем запрос

            // Устанавливаем сообщение для отправки
//            node.input = message;

            // Запускаем метод sendCriticalSectionRequest
            this.node.sendCriticalSectionRequest(message);

            return "Start send Message from API: " + message;
        } catch (RemoteException e) {
            e.printStackTrace();
            return "Failed to send message: " + e.getMessage();
        }
    }

//    @GetMapping("/join")
//    public String startNode() {
//        try {
//            String[] args = {"-ip", "localhost", "-p", "1999", "-myPort", "8082", "-n", "MyNode"}; // Пример аргументов
//            Node node = new Node(args, "MyNode");
//
//            System.out.println("Run Node FROM_JOIN_API");
//            new Thread(node::run).start();
//
//            return "Node started successfully!";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to start Node: " + e.getMessage();
//        }
//    }

    @PostMapping("/join")
    public String startNode(@RequestParam int port, int myPort) {
        try {
            // Используем переданный порт для создания нового узла
            String[] args = {"-ip", "192.168.0.157", "-p", String.valueOf(port), "-myPort", String.valueOf(myPort), "-n", "MyNode"};
            Node node = new Node(args, "MyNode");
            this.node = node;

            System.out.println("Run Node FROM_JOIN_API with port: " + myPort);
            new Thread(node::run).start();
            System.out.println("My Node: " + node);

            return "Node started successfully on port: " + myPort;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start Node: " + e.getMessage();
        }
    }
}