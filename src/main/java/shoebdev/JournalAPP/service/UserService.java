package shoebdev.JournalAPP.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shoebdev.JournalAPP.entity.User; // import the User entity
import shoebdev.JournalAPP.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

   


    // ...existing code...
    public void saveEntry(User user){
        userRepository.save(user);
    }
    // ...existing code...

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