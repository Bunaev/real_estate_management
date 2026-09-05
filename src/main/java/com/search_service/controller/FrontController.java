package com.search_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FrontController {
    @GetMapping({"/"})
    public String index() {
        return "index";
    }

    @GetMapping("/complex/{id}")
    public String complexDetail(@PathVariable Long id) {
        return "complex-detail";
    }
}