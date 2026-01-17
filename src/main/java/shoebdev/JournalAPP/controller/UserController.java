package shoebdev.JournalAPP.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;

import shoebdev.JournalAPP.entity.User;
import shoebdev.JournalAPP.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /user - Get all users (Admin only)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    // GET /user/profile - Get current logged-in user profile
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        if (user != null) {
            // Don't return password in response
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // GET /user/id/{id} - Get user by ID (Admin only)
    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable ObjectId id) {
        return userService.findById(id)
                .map(user -> {
                    user.setPassword(null); // Don't expose password
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // PUT /user/profile - Update current user profile
    @PutMapping("/profile")
    public ResponseEntity<User> updateCurrentUserProfile(@RequestBody User incoming) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User existing = userService.findByUserName(userName);

        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Update allowed fields (not password or roles through this endpoint)
        if (incoming.getUserName() != null && !incoming.getUserName().isBlank()) {
            // Check if new username is already taken
            if (!incoming.getUserName().equals(existing.getUserName()) &&
                userService.findByUserName(incoming.getUserName()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null); // Username already exists
            }
            existing.setUserName(incoming.getUserName());
        }

        userService.saveEntry(existing);
        existing.setPassword(null); // Don't return password
        return ResponseEntity.ok(existing);
    }

    // PUT /user/id/{id} - Update user by ID (Admin only)
    @PutMapping("/id/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable ObjectId id, @RequestBody User incoming) {
        User existing = userService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Update fields
        if (incoming.getUserName() != null && !incoming.getUserName().isBlank()) {
            // Check if new username is already taken
            if (!incoming.getUserName().equals(existing.getUserName()) &&
                userService.findByUserName(incoming.getUserName()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null); // Username already exists
            }
            existing.setUserName(incoming.getUserName());
        }

        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
            existing.setPassword(userService.encodePassword(incoming.getPassword()));
        }

        if (incoming.getRoles() != null) {
            existing.setRoles(incoming.getRoles());
        }

        userService.saveEntry(existing);
        existing.setPassword(null); // Don't return password
        return ResponseEntity.ok(existing);
    }

    // DELETE /user/id/{id} - Delete user by ID (Admin only)
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> deleteUserById(@PathVariable ObjectId id) {
        User existing = userService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found", "id", id.toHexString()));
        }

        userService.deleteById(id);
        return ResponseEntity.ok(new HashMap<>() {{
            put("success", true);
            put("message", "User deleted successfully");
            put("id", id.toHexString());
        }});
    }

    // DELETE /user/profile - Delete current user account
    @DeleteMapping("/profile")
    public ResponseEntity<Map<String, Object>> deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        if (user != null) {
            userService.deleteById(user.getId());
            return ResponseEntity.ok(new HashMap<>() {{
                put("success", true);
                put("message", "Account deleted successfully");
            }});
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "User not found"));
    }
}
