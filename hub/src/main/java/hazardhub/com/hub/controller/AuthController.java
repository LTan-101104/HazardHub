package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.AuthResponse;
import hazardhub.com.hub.model.dto.LoginRequest;
import hazardhub.com.hub.model.dto.UserRegistration;
import hazardhub.com.hub.model.dto.UserResponse;
import hazardhub.com.hub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration")
@Profile("!test")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistration request)
            throws ExecutionException, InterruptedException {

        UserResponse user = userService.registerUser(request);

        AuthResponse response = AuthResponse.builder()
                .user(user)
                .message("User registered successfully. Please sign in to get your token.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get a custom token", 
               description = "Returns a Firebase custom token. Exchange it for an ID token using Firebase REST API.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        String customToken = userService.createCustomToken(request.getEmail());

        AuthResponse response = AuthResponse.builder()
                .idToken(customToken)
                .message("Login successful. Use this custom token to get an ID token from Firebase.")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal String uid)
            throws ExecutionException, InterruptedException {

        UserResponse user = userService.getUserById(uid);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal String uid,
            @RequestBody UserResponse updates)
            throws ExecutionException, InterruptedException {

        UserResponse user = userService.updateUser(uid, updates);
        return ResponseEntity.ok(user);
    }
}
