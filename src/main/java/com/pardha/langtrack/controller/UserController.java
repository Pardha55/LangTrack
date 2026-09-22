package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

 @PostMapping
public User createUser(@RequestBody User user) {
    return repository.save(user);
}

    @GetMapping
    public List<User> getUsers() {
        return repository.findAll();
    }
}