package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.ShortUrlRepository;
import com.hitanshudhawan.sankshipt.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlServiceImpl(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Override
    public URL createShortUrl(String originalUrl, User user) {
        URL url = new URL();
        url.setOriginalUrl(originalUrl);
        url.setUser(user);

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

    @Override
    public void deleteShortUrl(String shortCode, User user) throws UrlNotFoundException {
        URL url = shortUrlRepository.findByShortCodeAndUser(shortCode, user)
                .orElseThrow(() -> new UrlNotFoundException(String.format("No URL mapping found for short code: %s owned by user: %s", shortCode, user.getEmail())));

        // Validate the short code against the original URL for security
        if (!ShortCodeGenerator.validateShortCode(shortCode, url.getOriginalUrl())) {
            throw new UrlNotFoundException(String.format("Short code '%s' failed validation for stored URL: %s", shortCode, url.getOriginalUrl()));
        }

        shortUrlRepository.delete(url);
    }

    @Override
    public boolean isUrlOwner(String shortCode, User user) throws UrlNotFoundException {
        URL url = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(String.format("No URL mapping found for short code: %s", shortCode)));
        
        return url.getUser().getEmail().equals(user.getEmail());
    }

}
