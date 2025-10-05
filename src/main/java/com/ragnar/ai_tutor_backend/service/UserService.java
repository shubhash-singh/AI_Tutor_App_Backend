package com.ragnar.ai_tutor_backend.service;

import com.ragnar.ai_tutor_backend.model.User;
import com.ragnar.ai_tutor_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** A service class to handle user utilities
 * 1.add or update user
 * 2. find user by google Id
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;


    // return the user details with the given googleId
    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    // save or update user
    public User saveOrUpdateUser(User user) {
        User oldUser = findByGoogleId(user.getGoogleId());

        if (oldUser != null) {
            oldUser.setEmail(user.getEmail());
            oldUser.setName(user.getName());
            oldUser.setGoogleId(user.getGoogleId());
            oldUser.setPhotoUrl(user.getPhotoUrl());
            oldUser.updateTimestamp();

            return userRepository.save(oldUser);  // saves and return the updated User object
        }
        else {
            return userRepository.save(user); // saves and returns the new User object
        }
    }


}
