package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets or creates a user by email. This is used when a user creates a URL
     * to ensure we have a User record in the api-server database.
     *
     * @param email the email of the user
     * @return the User entity
     */
    public User getOrCreateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    return userRepository.save(newUser);
                });
    }

}
