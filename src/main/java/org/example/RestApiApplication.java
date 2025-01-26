package org.example;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;



import javax.annotation.PostConstruct;
import java.util.Arrays;


@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RestApiApplication.class);

        // Слушатель для проверки параметров запуска
        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
            ConfigurableEnvironment env = event.getEnvironment();

            // Проверяем, передан ли порт через параметры
            String port = Arrays.stream(args)
                    .filter(arg -> arg.startsWith("--server.port="))
                    .map(arg -> arg.split("=")[1])
                    .findFirst()
                    .orElse(env.getProperty("server.port", "8080")); // По умолчанию 8080

            System.out.println("Server will start on port: " + port);
        });

        app.run(args); // Передаем аргументы в Spring Boot
    }
}
//
//@SpringBootApplication
//public class RestApiApplication {
////    public static void main(String[] args) {
////        SpringApplication.run(RestApiApplication.class, args);
////    }
//
//    public static void main(String[] args) {
//        SpringApplication app = new SpringApplication(RestApiApplication.class);
//
//        // Добавляем слушатель для установки IP-адреса
//        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
//            ConfigurableEnvironment env = event.getEnvironment();
//            String serverAddress = env.getProperty("server.address", "192.168.0.157"); // Default IP: 0.0.0.0
//            System.out.println("Server address set to: " + serverAddress);
//        });
//
//        app.run(args);
//    }
//}

//package org.example;
//
//        import org.example.Node;
//        import org.springframework.boot.ApplicationArguments;
//        import org.springframework.boot.SpringApplication;
//        import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//        import javax.annotation.PostConstruct;
//
//@SpringBootApplication
//public class RestApiApplication {
//
//    private final ApplicationArguments applicationArguments;
//    private Node node;
//
//    public RestApiApplication(ApplicationArguments applicationArguments) {
//        System.out.println("-----------------------------------------222 ");
//
//        this.applicationArguments = applicationArguments;
//    }
//
//    @PostConstruct
//    public void initializeNode() {
//
//        System.out.println("-----------------------------------------333 ");
//
////        if (!applicationArguments.containsOption("node.name") ||
////                !applicationArguments.containsOption("node.ip") ||
////                !applicationArguments.containsOption("node.port") ||
////                !applicationArguments.containsOption("node.myPort")) {
////            throw new IllegalArgumentException("Обязательные параметры node.name, node.ip, node.port и node.myPort отсутствуют.");
////        }
//
//        String name = applicationArguments.containsOption("node.name") &&
//                applicationArguments.getOptionValues("node.name") != null &&
//                !applicationArguments.getOptionValues("node.name").isEmpty()
//                ? applicationArguments.getOptionValues("node.name").get(0)
//                : "test";
//
//        String ip = (applicationArguments.containsOption("node.ip") &&
//                applicationArguments.getOptionValues("node.ip") != null &&
//                !applicationArguments.getOptionValues("node.ip").isEmpty())
//                ? applicationArguments.getOptionValues("node.ip").get(0)
//                : null;
//
//        String port = (applicationArguments.containsOption("node.port") &&
//                applicationArguments.getOptionValues("node.port") != null &&
//                !applicationArguments.getOptionValues("node.port").isEmpty())
//                ? applicationArguments.getOptionValues("node.port").get(0)
//                : "-1";
//
//        String myPort = (applicationArguments.containsOption("node.myPort") &&
//                applicationArguments.getOptionValues("node.myPort") != null &&
//                !applicationArguments.getOptionValues("node.myPort").isEmpty())
//                ? applicationArguments.getOptionValues("node.myPort").get(0)
//                : "-1";
//
//        String[] nodeArgs = {"-ip", ip, "-p", port, "-myPort", myPort, "-n", name};
//
//        System.out.println("----------------------------------------4444 ");
//
//        // Здесь создается объект Node
//        this.node = new Node(nodeArgs, name);
//
//        startNode(); // Запуск в отдельном потоке
//    }
//
//    // Запуск Node в отдельном потоке
//    private void startNode() {
//        try {
//            System.out.println("Запуск ноды...");
//            node.run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Node getNode() {
//        return node;
//    }
//
//    public static void main(String[] args) {
//        System.out.println("-----------------------------------------11111 ");
//        SpringApplication.run(RestApiApplication.class, args);
//        throw new IllegalArgumentException("Обязательные параметры node.name, node.ip, node.port и node.myPort отсутствуют111.");
//    }
//}