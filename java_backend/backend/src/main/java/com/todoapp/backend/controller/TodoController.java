package com.todoapp.backend.controller;


import com.todoapp.backend.model.Todo;
import com.todoapp.backend.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        String userId = jwt.getSubject();

        System.out.println(" Próba utworzenia zadania:");
        System.out.println("️ Tytuł: " + todo.getTitle());
        System.out.println("️ Done: " + todo.isDone());
        System.out.println("️ createdAt: " + todo.getCreatedAt());
        System.out.println(" userId z tokena: " + userId);

        try {
            todo.setUserId(userId);
            return todoRepository.save(todo);
        } catch (Exception e) {
            System.err.println(" Błąd podczas zapisu TODO: " + e.getMessage());
            e.printStackTrace();
            throw e; // żeby pojawił się też jako 500
        }
    }




    //oznaczenie zad jako zrobione
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo, @AuthenticationPrincipal Jwt jwt) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        if (!todo.getUserId().equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie twoje zadanie!");
        }
        todo.setTitle(updatedTodo.getTitle());
        todo.setDone(updatedTodo.isDone());
        return todoRepository.save(todo);
    }


    //usuwanie zadan
    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Todo todo = todoRepository.findById(id).orElseThrow();
        if (!todo.getUserId().equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie twoje zadanie!");
        }
        todoRepository.delete(todo);
    }


}
