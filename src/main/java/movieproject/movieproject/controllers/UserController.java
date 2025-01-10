package movieproject.movieproject.controllers;

import movieproject.movieproject.entity.User;
import movieproject.movieproject.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin // Permitir solicitudes de diferentes dominios
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // Validación inicial, por ejemplo, para evitar duplicados de correo
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Email ya registrado
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedUser.setId(id); // Asegurar que se actualice el usuario correcto
        User savedUser = userRepository.save(updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
