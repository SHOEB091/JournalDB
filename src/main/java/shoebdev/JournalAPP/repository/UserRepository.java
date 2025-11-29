package shoebdev.JournalAPP.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import shoebdev.JournalAPP.entity.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {

    // Derived query must match field name exactly: `userName` (not `username`)
    User findByUserName(String userName);

}
