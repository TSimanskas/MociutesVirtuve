package Project.KodoKeliasLasProject.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/main")
    public String main() {
        return "recipes/list";
    }

}
