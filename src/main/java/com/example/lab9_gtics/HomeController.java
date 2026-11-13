package com.example.lab9_gtics;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "html"; // Charge src/main/resources/templates/html.html
    }
}