package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import jakarta.servlet.http.HttpServletRequest;

public interface ClickAnalyticsService {

    Click recordClick(URL url, HttpServletRequest request);

    Long getClickCountForUrl(URL url);

//    List<> getClicksForUrl(URL url);

}
