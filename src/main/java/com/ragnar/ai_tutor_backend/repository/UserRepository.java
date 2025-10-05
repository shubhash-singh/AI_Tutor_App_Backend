package com.ragnar.ai_tutor_backend.repository;

import com.ragnar.ai_tutor_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByGoogleId(String googleId);
}
