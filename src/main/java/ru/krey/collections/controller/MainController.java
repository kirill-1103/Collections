package ru.krey.collections.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import ru.krey.collections.auth.Auth;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.Item;
import ru.krey.collections.model.User;
import ru.krey.collections.service.CollectionService;
import ru.krey.collections.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@Slf4j
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
    public String getMain(@RequestParam ArrayList<Item> searchResult, Model model){
        if(searchResult!=null){
            model.addAttribute("searchResult",searchResult);
        }
        log.info(Integer.toString(searchResult.size()));
//        model.addAttribute("name",auth.getCurrentUserLogin());
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
