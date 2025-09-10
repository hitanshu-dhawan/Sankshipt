package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.repositories.ClickRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Override
    public Page<Click> getClicksForUrl(URL url, Integer pageNumber, Integer pageSize, String sortOrder) {

        // Set default values
        int page = pageNumber != null ? pageNumber : 0;
        int size = pageSize != null ? pageSize : 20;

        // Create sort - default to DESC by clickedAt
        Sort sort = "ASC".equalsIgnoreCase(sortOrder)
                ? Sort.by(Sort.Order.asc("clickedAt"))
                : Sort.by(Sort.Order.desc("clickedAt"));

        return clickRepository.findByUrl(url, PageRequest.of(page, size, sort));
    }

}
