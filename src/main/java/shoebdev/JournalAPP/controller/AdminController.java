package shoebdev.JournalAPP.controller;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shoebdev.JournalAPP.entity.User;
import shoebdev.JournalAPP.service.UserService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> allUsers = userService.getAll();
            if (allUsers != null && !allUsers.isEmpty()) {
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving users: " + e.getMessage(),
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable ObjectId id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving user: " + e.getMessage(),
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable ObjectId id, @RequestBody User user) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isPresent()) {
                user.setId(id);
                // Preserve password if not provided in update
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    user.setPassword(existingUser.get().getPassword());
                } else {
                    // Encode new password if provided
                    user.setPassword(userService.encodePassword(user.getPassword()));
                }
                userService.saveEntry(user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating user: " + e.getMessage(),
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable ObjectId id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                userService.deleteById(id);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting user: " + e.getMessage(),
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/count")
    public ResponseEntity<?> getUserCount() {
        try {
            List<User> allUsers = userService.getAll();
            return new ResponseEntity<>("Total users: " + allUsers.size(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error counting users: " + e.getMessage(),
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable ObjectId id, @RequestBody UserRoleUpdateRequest request) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setRoles(request.getRoles());
                userService.saveEntry(user);
                return new ResponseEntity<>("User role updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating user role: " + e.getMessage(),
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper class for role update requests
    public static class UserRoleUpdateRequest {
        private List<String> roles;

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
