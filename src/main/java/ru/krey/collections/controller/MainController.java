package ru.krey.collections.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.krey.collections.auth.Auth;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.User;
import ru.krey.collections.service.CollectionService;
import ru.krey.collections.service.UserService;

import java.util.List;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    private final Auth auth;

    @Autowired
    private final CollectionService collectionService;

    @Autowired
    private final UserService userService;

    public MainController(Auth auth, CollectionService collectionService, UserService userService) {
        this.auth = auth;
        this.collectionService = collectionService;
        this.userService = userService;
    }

    @GetMapping
    public String getMain(Model model){
        model.addAttribute("name",auth.getCurrentUserLogin());
        return "main";
    }

    @GetMapping("/account")
    public String getAccount(Model model){
        User user = userService.getUserByLogin(auth.getCurrentUserLogin());
        List<Collection> collections = collectionService.getCollectionsByCreator(user);
        model.addAttribute("collections",collections);
        return "account";
    }

}
