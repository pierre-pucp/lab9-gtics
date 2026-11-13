package com.example.lab9_gtics;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller 
@RequestMapping("/")
public class Controller {
    
    @GetMapping("/")
    public String pagina(Model model) {
        //List<File> images = …
        //model.addAttributes("images", images)
        return "html";
    }
}
