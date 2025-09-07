package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.repositories.ClickRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ClickAnalyticsServiceImpl implements ClickAnalyticsService {

    private final ClickRepository clickRepository;

    public ClickAnalyticsServiceImpl(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

    @Override
    public Click recordClick(URL url, HttpServletRequest request) {
        Click click = new Click();
        click.setUrl(url);
        click.setUserAgent(request.getHeader("User-Agent"));

        return clickRepository.save(click);
    }

    @Override
    public Long getClickCountForUrl(URL url) {
        return clickRepository.countByUrlShortCode(url.getShortCode());
    }

}
