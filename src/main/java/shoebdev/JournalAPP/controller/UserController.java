package shoebdev.JournalAPP.controller;


import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;

import shoebdev.JournalAPP.entity.User;
import shoebdev.JournalAPP.service.UserService;


@RestController
@RequestMapping("/user")  // this request mapping add the mapping on the class
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping // GET /user -> 200 OK list of users
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
    
    @PostMapping // POST /user -> 201 Created
    public ResponseEntity<User> create(@RequestBody User user) {
        userService.saveEntry(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/id/{id}") // GET /user/id/{id} -> 200 OK or 404
    public ResponseEntity<User> getById(@PathVariable ObjectId id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/id/{id}") // PUT /user/id/{id} -> 200 OK updated user or 404
    public ResponseEntity<User> updateEntry(@PathVariable ObjectId id, @RequestBody User incoming){
        User existing = userService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Partial update: only overwrite provided non-empty values
        if (incoming.getUserName() != null && !incoming.getUserName().isBlank()) {
            existing.setUserName(incoming.getUserName());
        }
        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
            existing.setPassword(incoming.getPassword());
        }
        userService.saveEntry(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/id/{id}") // DELETE /user/id/{id} -> 200 OK with message or 404
    public ResponseEntity<Map<String,Object>> deleteById(@PathVariable ObjectId id) {
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



}
