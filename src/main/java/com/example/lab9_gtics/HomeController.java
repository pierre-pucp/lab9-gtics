package com.example.lab9_gtics;

import java.io.File;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {
    
    @GetMapping("/")
    public String pagina(Model model) {
        File image = new File("default-image.jpg");
        model.addAttribute("default-image", image);
        return "html";
    }
}
