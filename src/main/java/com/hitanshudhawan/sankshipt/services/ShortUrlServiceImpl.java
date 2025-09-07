package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.repositories.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ShortUrlRepository shortUrlRepository;

    public ShortUrlServiceImpl(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Override
    public URL createShortUrl(String originalUrl) {
        URL url = new URL();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(generateShortCode());
        return shortUrlRepository.save(url);
    }

    @Override
    public Optional<URL> findByShortCode(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode);
    }

    private String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            shortCode.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return shortCode.toString();
    }

}
