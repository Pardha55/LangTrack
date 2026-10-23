
package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pardha.langtrack.entity.Progress;
import com.pardha.langtrack.entity.ProgressSummary;
import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.UserRepository;
import com.pardha.langtrack.service.ProgressService;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService service;
    private final UserRepository userRepository;

    public ProgressController(
            ProgressService service,
            UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/user/{userId}")
    public List<Progress> getProgressByUser(
            @PathVariable Long userId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);
        verifyOwnership(user, userId);

        return service.getProgressByUser(userId);
    }

    @GetMapping("/user/{userId}/summary")
    public ProgressSummary getUserSummary(
            @PathVariable Long userId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);
        verifyOwnership(user, userId);

        return service.getUserSummary(userId);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in first");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));
    }

    private void verifyOwnership(User user, Long requestedUserId) {
        if (!user.getId().equals(requestedUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another user's progress");
        }
    }
}
