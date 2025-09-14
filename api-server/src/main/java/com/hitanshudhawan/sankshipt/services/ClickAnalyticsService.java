package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ClickAnalyticsService {

    /**
     * Records a click event for a given URL, capturing analytics data from the HTTP request.
     *
     * @param url the URL that was clicked
     * @param request the HTTP request containing user agent, IP address, and other metadata
     * @return the recorded Click entity
     */
    Click recordClick(URL url, HttpServletRequest request);

    /**
     * Retrieves the total number of clicks for a specific URL.
     *
     * @param url the URL to get the click count for
     * @return the total number of clicks for the URL
     */
    Long getClickCountForUrl(URL url);

    /**
     * Retrieves a paginated list of clicks for a specific URL with optional sorting.
     *
     * @param url the URL to get clicks for
     * @param pageNumber the page number to retrieve (0-based)
     * @param pageSize the number of clicks per page
     * @param sortOrder the sort order for the results (e.g., "asc" or "desc")
     * @return a paginated list of Click entities
     */
    Page<Click> getClicksForUrl(URL url, Integer pageNumber, Integer pageSize, String sortOrder);

}
