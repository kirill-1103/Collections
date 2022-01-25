package ru.krey.collections.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.krey.collections.auth.Auth;

@Controller
public class MainController {

    @Autowired
    private final Auth auth;

    public MainController(Auth auth) {
        this.auth = auth;
    }

    @GetMapping
    public String getMain(Model model){
        model.addAttribute("name",auth.getCurrentUserLogin());
        return "main";
    }

    @GetMapping("/account")
    public String getAccount(){
        return "account";
    }

//    public String getCurrentUserLogin() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return auth.getName();
//    }
}
