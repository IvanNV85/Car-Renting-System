package fu.carrentingsystem.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorld {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("message", "Welcome to rental system");
        return "hello";
    }

}
