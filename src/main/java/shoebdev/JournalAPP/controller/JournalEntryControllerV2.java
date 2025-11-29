package shoebdev.JournalAPP.controller;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoebdev.JournalAPP.entity.JournalEntry;
import shoebdev.JournalAPP.entity.User;
import shoebdev.JournalAPP.service.JournalEntryService;
import shoebdev.JournalAPP.service.UserService;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")  // this request mapping add the mapping on the class
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

     @Autowired
    private UserService userService;


    //private Map<String, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping("{userName}") // GET /journal -> 200 OK on success
    public ResponseEntity<List<JournalEntry>> getAll(@PathVariable String userName) {
        User user = userService.findByUserName(userName); // just to avoid unused variable warning
        List<JournalEntry> list = user.getJournalEntries();
        // Return 200 OK with the list in the response body
        return ResponseEntity.ok(list);
    }

    @PostMapping("{userName}") // POST /journal/{userName} -> 201 Created on successful creation
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            // User not found -> 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        myEntry.setDate(LocalDateTime.now());
        // Persist entry first when using @DBRef in User.journalEntries
        journalEntryService.saveEntry(myEntry);
        user.getJournalEntries().add(myEntry);
        userService.saveEntry(user);

        // Return 201 Created with the saved entity in the response body
        return ResponseEntity.status(HttpStatus.CREATED).body(myEntry);
    }

    //Path variable or Query Parameters so we use path variables

    @GetMapping("{userName}/id/{myId}") // GET /journal/{userName}/id/{id}
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable String userName,
                                                            @PathVariable ObjectId myId) {
        User user = userService.findByUserName(userName);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return user.getJournalEntries().stream()
                .filter(e -> myId.equals(e.getId()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("{userName}/id/{myId}") // DELETE /journal/{userName}/id/{id}
    public ResponseEntity<Map<String, Object>> deleteJournalEntryById(@PathVariable String userName,
                                                                      @PathVariable ObjectId myId) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found", "userName", userName));
        }
        boolean removed = user.getJournalEntries().removeIf(e -> myId.equals(e.getId()));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Journal entry not found", "id", myId.toHexString()));
        }
        userService.saveEntry(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Journal entry deleted successfully", "id", myId.toHexString()));
    }

    @PutMapping("{userName}/id/{id}") // PUT /journal/{userName}/id/{id}
    public ResponseEntity<JournalEntry> updateJournalEntryById(@PathVariable String userName,
                                                               @PathVariable ObjectId id,
                                                               @RequestBody JournalEntry newEntry) {
        User user = userService.findByUserName(userName);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        JournalEntry old = user.getJournalEntries().stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst().orElse(null);
        if (old == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isBlank() ? newEntry.getTitle() : old.getTitle());
        old.setContent(newEntry.getContent() != null && !newEntry.getContent().isBlank() ? newEntry.getContent() : old.getContent());
        userService.saveEntry(user);
        return ResponseEntity.ok(old);
    }

}
