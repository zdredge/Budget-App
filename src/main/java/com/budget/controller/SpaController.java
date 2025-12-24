package com.budget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    /**
     * Forward root path to index.html for SPA.
     */
    @GetMapping("/")
    public String forward() {
        return "forward:/index.html";
    }
}

