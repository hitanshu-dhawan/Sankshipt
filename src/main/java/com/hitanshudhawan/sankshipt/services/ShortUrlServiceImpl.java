package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.repositories.ShortUrlRepository;
import com.hitanshudhawan.sankshipt.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlServiceImpl(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Override
    public URL createShortUrl(String originalUrl) {
        URL url = new URL();
        url.setOriginalUrl(originalUrl);

        // Save first to get the auto-generated ID
        URL savedUrl = shortUrlRepository.save(url);

        // Generate short code using the ID and original URL
        String shortCode = ShortCodeGenerator.generateShortCode(savedUrl.getId(), originalUrl);
        savedUrl.setShortCode(shortCode);

        // Save again with the generated short code
        return shortUrlRepository.save(savedUrl);
    }

    @Override
    public URL resolveShortCode(String shortCode) throws UrlNotFoundException {
        URL url = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(String.format("No URL mapping found for short code: %s", shortCode)));

        // Validate the short code against the original URL for security
        if (!ShortCodeGenerator.validateShortCode(shortCode, url.getOriginalUrl())) {
            // Short code doesn't match the original URL (possibly tampered)
            throw new UrlNotFoundException(String.format("Short code '%s' failed validation for stored URL: %s", shortCode, url.getOriginalUrl()));
        }

        return url;
    }

}
