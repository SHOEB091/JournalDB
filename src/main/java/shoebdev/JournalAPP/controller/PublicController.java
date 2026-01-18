package shoebdev.JournalAPP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import shoebdev.JournalAPP.entity.User;
import shoebdev.JournalAPP.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user
     * POST /public/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody User user) {
        try {
            // Check if user already exists
            if (userService.existsByUserName(user.getUserName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "Username already exists"));
            }

            // Validate input
            if (user.getUserName() == null || user.getUserName().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Username is required"));
            }

            if (user.getPassword() == null || user.getPassword().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Password is required"));
            }

            // Create user
            userService.saveNewUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "User registered successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Register a new admin user (for testing purposes)
     * POST /public/admin/signup
     */
    @PostMapping("/admin/signup")
    public ResponseEntity<Map<String, Object>> adminSignup(@RequestBody User user) {
        try {
            // Check if user already exists
            if (userService.existsByUserName(user.getUserName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "Username already exists"));
            }

            // Validate input
            if (user.getUserName() == null || user.getUserName().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Username is required"));
            }

            if (user.getPassword() == null || user.getPassword().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Password is required"));
            }

            // Create admin user
            userService.saveNewAdmin(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Admin user registered successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Admin registration failed: " + e.getMessage()));
        }
    }

    /**
     * Login user
     * POST /public/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            String userName = credentials.get("userName");
            String password = credentials.get("password");

            // Validate input
            if (userName == null || userName.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Username is required"));
            }

            if (password == null || password.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Password is required"));
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, password)
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userService.findByUserName(userName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", Map.of(
                "id", user.getId().toHexString(),
                "userName", user.getUserName(),
                "roles", user.getRoles()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Invalid username or password"));
        }
    }

    /**
     * Logout user
     * POST /public/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    /**
     * Health check endpoint
     * GET /public/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Journal App is running",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
