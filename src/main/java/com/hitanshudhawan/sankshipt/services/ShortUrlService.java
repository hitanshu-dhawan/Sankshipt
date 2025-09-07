package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.URL;

import java.util.Optional;

public interface ShortUrlService {

    URL createShortUrl(String originalUrl);

    Optional<URL> findByShortCode(String shortCode);

}
