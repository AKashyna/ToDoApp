package com.todoapp.backend.repository;

import com.todoapp.backend.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // funckja ma zwracac liste zadan konkretnego uzytkownika
    List<Todo> findByUserId(String userId);
}