package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;

public interface ShortUrlService {

    URL createShortUrl(String originalUrl, User user);

    URL resolveShortCode(String shortCode) throws UrlNotFoundException;

    void deleteShortUrl(String shortCode, User user) throws UrlNotFoundException;

    boolean isUrlOwner(String shortCode, User user) throws UrlNotFoundException;

}
