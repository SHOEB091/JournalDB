package shoebdev.JournalAPP.controller;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoebdev.JournalAPP.entity.JournalEntry;
import shoebdev.JournalAPP.entity.User;
import shoebdev.JournalAPP.service.JournalEntryService;
import shoebdev.JournalAPP.service.UserService;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

     @Autowired
    private UserService userService;


    //private Map<String, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping("{userName}")
    public ResponseEntity<List<JournalEntry>> getAll(@PathVariable String userName) {
        // Check if user is authenticated and requesting their own data, or if it's a public read
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
                                 !authentication.getPrincipal().equals("anonymousUser");

        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Allow reading own journal entries, or if admin
        if (isAuthenticated) {
            String currentUserName = authentication.getName();
            if (currentUserName.equals(userName) || hasAdminRole(authentication)) {
                return ResponseEntity.ok(user.getJournalEntries());
            }
        }

        // For now, allow public reading (you can change this to private later)
        return ResponseEntity.ok(user.getJournalEntries());
    }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName) {
        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUserName = authentication.getName();

        // Users can only create entries for themselves (unless admin)
        if (!currentUserName.equals(userName) && !hasAdminRole(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(null); // Cannot create entries for other users
        }

        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        myEntry.setDate(LocalDateTime.now());
        journalEntryService.saveEntry(myEntry);
        user.getJournalEntries().add(myEntry);
        userService.saveEntry(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(myEntry);
    }

    //Path variable or Query Parameters so we use path variables

    @GetMapping("{userName}/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable String userName,
                                                            @PathVariable ObjectId myId) {
        // Check authentication for reading specific entries
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
                                 !authentication.getPrincipal().equals("anonymousUser");

        User user = userService.findByUserName(userName);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // Allow reading own entries or if admin
        if (isAuthenticated) {
            String currentUserName = authentication.getName();
            if (currentUserName.equals(userName) || hasAdminRole(authentication)) {
                return user.getJournalEntries().stream()
                        .filter(e -> myId.equals(e.getId()))
                        .findFirst()
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            }
        }

        // For now, allow public reading of specific entries too
        return user.getJournalEntries().stream()
                .filter(e -> myId.equals(e.getId()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("{userName}/id/{myId}")
    public ResponseEntity<Map<String, Object>> deleteJournalEntryById(@PathVariable String userName,
                                                                      @PathVariable ObjectId myId) {
        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Authentication required"));
        }

        String currentUserName = authentication.getName();

        // Users can only delete their own entries (unless admin)
        if (!currentUserName.equals(userName) && !hasAdminRole(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Cannot delete other users' entries"));
        }

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

    @PutMapping("{userName}/id/{id}")
    public ResponseEntity<JournalEntry> updateJournalEntryById(@PathVariable String userName,
                                                               @PathVariable ObjectId id,
                                                               @RequestBody JournalEntry newEntry) {
        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUserName = authentication.getName();

        // Users can only update their own entries (unless admin)
        if (!currentUserName.equals(userName) && !hasAdminRole(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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

    /**
     * Helper method to check if user has ADMIN role
     */
    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
