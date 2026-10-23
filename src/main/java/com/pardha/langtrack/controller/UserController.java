
package com.pardha.langtrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pardha.langtrack.dto.StreakSummary;
import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    

   
@GetMapping("/{userId}/streak")
public StreakSummary getUserStreak(
        @PathVariable Long userId,
        Authentication authentication) {

    if (authentication == null
            || !authentication.isAuthenticated()) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Please log in first");
    }

    User user = repository.findByUsername(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found"));

    if (!user.getId().equals(userId)) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot access another user's streak");
    }

    return new StreakSummary(
            user.getId(),
            user.getCurrentStreak(),
            user.getLongestStreak(),
            user.getLastStreakDate()
    );
}

    }

