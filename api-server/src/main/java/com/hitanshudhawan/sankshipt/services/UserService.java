package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.User;

public interface UserService {

    /**
     * Gets or creates a user by email. This is used when a user creates a URL
     * to ensure we have a User record in the api-server database.
     *
     * @param email the email of the user
     * @return the User entity
     */
    User getOrCreateUser(String email);

}
