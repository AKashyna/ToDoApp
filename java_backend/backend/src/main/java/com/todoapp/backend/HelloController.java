package com.todoapp.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello from public endpoint!";
    }

    @GetMapping("/private/hello")
    public String privateHello() {
        return "Hello from protected endpoint!";
    }
}
