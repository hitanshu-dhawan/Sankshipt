package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.User;

public interface AuthenticationService {

    /**
     * Gets the current authenticated user from the security context
     * @return the current User or null if not authenticated
     */
    User getCurrentUser();

}
