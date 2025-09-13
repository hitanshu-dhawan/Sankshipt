package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;

public interface ShortUrlService {

    URL createShortUrl(String originalUrl);

    URL resolveShortCode(String shortCode) throws UrlNotFoundException;

    void deleteShortUrl(String shortCode) throws UrlNotFoundException;

}
