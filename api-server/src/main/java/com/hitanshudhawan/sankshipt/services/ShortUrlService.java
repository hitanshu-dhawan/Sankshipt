package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;

public interface ShortUrlService {

    /**
     * Creates a short URL for the given original URL and associates it with the specified user.
     *
     * @param originalUrl the original long URL to be shortened
     * @param user the user who is creating the short URL
     * @return the created URL entity containing the short code and original URL
     */
    URL createShortUrl(String originalUrl, User user);

    /**
     * Resolves a short code to retrieve the corresponding URL entity.
     *
     * @param shortCode the short code to resolve
     * @return the URL entity associated with the short code
     * @throws UrlNotFoundException if no URL is found for the given short code
     */
    URL resolveShortCode(String shortCode) throws UrlNotFoundException;

    /**
     * Deletes a short URL if the specified user is the owner of the URL.
     *
     * @param shortCode the short code of the URL to delete
     * @param user the user requesting the deletion
     * @throws UrlNotFoundException if no URL is found for the given short code
     */
    void deleteShortUrl(String shortCode, User user) throws UrlNotFoundException;

    /**
     * Checks if the specified user is the owner of the URL with the given short code.
     *
     * @param shortCode the short code to check ownership for
     * @param user the user to verify ownership against
     * @return true if the user owns the URL, false otherwise
     * @throws UrlNotFoundException if no URL is found for the given short code
     */
    boolean isUrlOwner(String shortCode, User user) throws UrlNotFoundException;

}
