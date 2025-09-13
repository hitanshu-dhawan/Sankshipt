package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ClickAnalyticsService {

    Click recordClick(URL url, HttpServletRequest request);

    Long getClickCountForUrl(URL url);

    Page<Click> getClicksForUrl(URL url, Integer pageNumber, Integer pageSize, String sortOrder);

}
