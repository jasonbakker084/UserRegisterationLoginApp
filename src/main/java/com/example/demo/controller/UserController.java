package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @GetMapping("/users")
    public List<User> allUsers() {
        return repository.findAll();
    }

    @GetMapping(path = {"/users/{id}"})
    public ResponseEntity<User> findById(@PathVariable long id) {
        return repository.findById(id).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/count")
    public Long count() {
        return repository.count();
    }

    @PostMapping(value = "/add/user")
    public User create(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping(value = "/users/{id}")
    public ResponseEntity<User> update(@PathVariable("id") long id, @RequestBody User user) {
        return repository.findById(id).map(record -> {
            record.setFirstName(user.getFirstName());
            record.setLastName(user.getLastName());
            record.setEmail(user.getEmail());
            record.setCity(user.getCity());
            record.setCountry(user.getCountry());
            record.setUserType(user.getUserType());
            User updated = repository.save(record);
            return ResponseEntity.ok().body(updated);
        }).orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable String id) {

        Long userId = Long.parseLong(id);
        repository.deleteById(userId);
    }
}