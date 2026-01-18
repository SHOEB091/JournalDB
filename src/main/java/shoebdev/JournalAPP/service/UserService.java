package shoebdev.JournalAPP.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shoebdev.JournalAPP.entity.User; // import the User entity
import shoebdev.JournalAPP.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ...existing code...
    public void saveEntry(User user){
        userRepository.save(user);
    }

    public void saveNewUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);
    }

    public void saveNewAdmin(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("ADMIN"));
        userRepository.save(user);
    }

    /**
     * Encode password using BCrypt
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verify password against encoded password
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Check if user exists by username
     */
    public boolean existsByUserName(String userName) {
        return userRepository.findByUserName(userName) != null;
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id ){
        return userRepository.findById(id);
    }

   
    public User findByUserName(String userName){
         // Match repository method to the actual field name `userName`
         return userRepository.findByUserName(userName);

    }

    public void deleteById(ObjectId id){
        userRepository.deleteById(id);
    }
}