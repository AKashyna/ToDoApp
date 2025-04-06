package com.todoapp.backend;

import com.todoapp.backend.repository.UserRepository;
import com.todoapp.backend.model.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping("/google")
    public ResponseEntity<String> receiveToken(@AuthenticationPrincipal Jwt jwt) {
        String id = jwt.getSubject(); // sub
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        String picture = jwt.getClaim("picture");

        User user = new User(id, email, name, picture);
        userRepository.save(user);

        return ResponseEntity.ok("Zalogowano użytkownika: " + name);
    }



    @GetMapping("/me")
    public ResponseEntity<String> authenticatedUser(Principal principal) {
        return ResponseEntity.ok("Zalogowany jako: " + principal.getName());
    }


}
