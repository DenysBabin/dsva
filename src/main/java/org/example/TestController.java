package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/node")
public class TestController {

    @GetMapping("/test")
    public String test() {
        System.out.println("Test endpoint reached"); // Лог в консоль
        return "Server is running!";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam String message) {
        System.out.println("Received message: " + message); // Логируем запрос
        return "Connection successful! Received message: " + message;
    }
}