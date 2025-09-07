package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;

import java.util.Optional;

public interface ShortUrlService {

    URL createShortUrl(String originalUrl);

    URL resolveShortCode(String shortCode) throws UrlNotFoundException;

}
