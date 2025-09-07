package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class UrlController {

    private final ShortUrlService shortUrlService;

    public UrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToUrl(@PathVariable String shortCode) throws UrlNotFoundException {
        URL url = shortUrlService.resolveShortCode(shortCode);

        RedirectView redirectView = new RedirectView();
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setUrl(url.getOriginalUrl());
        return redirectView;
    }

}
