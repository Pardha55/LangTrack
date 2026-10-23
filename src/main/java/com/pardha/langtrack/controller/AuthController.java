
package com.pardha.langtrack.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.dto.LoginRequest;
import com.pardha.langtrack.dto.SignUpRequest;
import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.UserRepository;
import com.pardha.langtrack.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository;

public AuthController(
        AuthService authService,
        UserRepository userRepository,
        SecurityContextRepository securityContextRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
    this.securityContextRepository = securityContextRepository;
}

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(
            @RequestBody SignUpRequest request) {

        User user = authService.signUp(request);

        Map<String, Object> response = Map.of(
                "message", "Account created successfully",
                "userId", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(
        @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse) {

    User user = authService.login(request);

    Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    AuthorityUtils.createAuthorityList("ROLE_USER")
            );

    SecurityContext context =
            SecurityContextHolder.createEmptyContext();

    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    securityContextRepository.saveContext(
            context,
            httpRequest,
            httpResponse
    );

    Map<String, Object> response = Map.of(
            "message", "Login successful",
            "userId", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail()
    );

    return ResponseEntity.ok(response);
}

    
@GetMapping("/me")
public ResponseEntity<Map<String, Object>> currentUser() {

    var authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Not logged in"));
    }

    var userOptional =
            userRepository.findByUsername(authentication.getName());

    if (userOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "User not found"));
    }

    var user = userOptional.get();

    return ResponseEntity.ok(Map.of(
            "message", "Session is authenticated",
            "userId", user.getId(),
            "username", user.getUsername()
    ));
}

}
