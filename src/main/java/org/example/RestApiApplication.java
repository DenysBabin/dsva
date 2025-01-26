package org.example;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RestApiApplication.class);

        // Добавляем слушатель для настройки порта и IP
        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
            ConfigurableEnvironment env = event.getEnvironment();

            // Получаем порт из аргументов командной строки или используем по умолчанию 8080
            String port = Arrays.stream(args)
                    .filter(arg -> arg.startsWith("--server.port="))
                    .map(arg -> arg.split("=")[1])
                    .findFirst()
                    .orElse(env.getProperty("server.port", "8080")); // По умолчанию порт 8080

            // Получаем локальный IP-адрес
            String localIP;
            try {
                localIP = getLocalIP();
            } catch (SocketException e) {
                throw new RuntimeException("Failed to retrieve local IP address", e);
            }

            // Выводим информацию о сервере
            System.out.println("Server will start on IP: " + localIP + ", Port: " + port);
        });

        // Запускаем приложение Spring Boot
        app.run(args);
    }

    /**
     * Метод для получения локального IP-адреса машины.
     * @return Строка с локальным IPv4-адресом
     * @throws SocketException если не удалось получить IP-адрес
     */
    private static String getLocalIP() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            // Игнорируем loopback-адреса и неактивные интерфейсы
            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    return address.getHostAddress(); // Возвращаем первый найденный IPv4-адрес
                }
            }
        }
        throw new RuntimeException("No suitable network interface found");
    }
}