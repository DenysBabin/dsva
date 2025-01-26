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
    public String startNode(@RequestParam int port, @RequestParam int myPort, @RequestParam String ip, @RequestParam Boolean firstUser, @RequestParam String name) {
        try {
            String[] args;
            if (firstUser) {
                // Если это первый пользователь, не требуется IP и порт другого узла
                args = new String[]{"-myPort", String.valueOf(myPort), "-n", name};
            } else {
                // Если это не первый пользователь, подключаемся к указанному узлу
                args = new String[]{"-ip", ip, "-p", String.valueOf(port), "-myPort", String.valueOf(myPort), "-n", name};
            }
            // Используем переданный порт для создания нового узла
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

    @PostMapping("/leave")
    public String leaveNode() {
        try {
            if (this.node == null) {
                return "No node is currently active!";
            }

            System.out.println("Stopping Node: " + this.node.getMyAddress());
            this.node.handleExit(); // Завершаем работу узла
            this.node = null;

            return "Node has successfully left the network.";
        } catch (RemoteException e) {
            e.printStackTrace();
            return "Failed to leave the network: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while leaving the network: " + e.getMessage();
        }
    }

    @PostMapping("/kill")
    public String killNode() {
        try {
            if (this.node == null) {
                return "No node is currently active!";
            }

            // Логируем завершение работы узла
            System.out.println("Killing Node: " + this.node.getMyAddress());

            // Завершаем работу узла без дополнительных операций
            System.exit(1);

            return "Node has been successfully killed.";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while killing the node: " + e.getMessage();
        }
    }
}