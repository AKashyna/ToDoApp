package com.todoapp.backend.controller;


import com.todoapp.backend.model.Todo;
import com.todoapp.backend.repository.TodoRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<Todo> getTodos(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return todoRepository.findByUserId(userId);
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo, @AuthenticationPrincipal Jwt jwt) {
        todo.setUserId(jwt.getSubject());
        return todoRepository.save(todo);
    }
}
