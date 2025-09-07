package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
public class UrlController {

    private final ShortUrlService shortUrlService;

    public UrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToUrl(@PathVariable String shortCode) {
        Optional<URL> urlOptional = shortUrlService.findByShortCode(shortCode);
        
        if (urlOptional.isPresent()) {
            URL url = urlOptional.get();
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(url.getOriginalUrl());
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return redirectView;
        } else {
            // Return a redirect to a 404 page or error page
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/error/404");
            redirectView.setStatusCode(HttpStatus.NOT_FOUND);
            return redirectView;
        }
    }

}
