package com.hitanshudhawan.sankshipt.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Root controller that handles redirection from the application root to the Swagger UI.
 * This provides a convenient way for users to access the API documentation by navigating to the root URL.
 * Hidden from Swagger documentation as it's an internal redirect endpoint.
 */
@RestController
@Hidden
public class RootController {

    @GetMapping("/")
    @Hidden
    public RedirectView redirectToSwaggerUI() {
        RedirectView redirectView = new RedirectView();
        redirectView.setStatusCode(HttpStatus.FOUND); // 302 redirect
        redirectView.setUrl("/swagger-ui/index.html");
        return redirectView;
    }
}
