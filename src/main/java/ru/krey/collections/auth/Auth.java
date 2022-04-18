package ru.krey.collections.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.krey.collections.model.User;
import ru.krey.collections.service.UserService;

@Component
public class Auth {

    @Autowired
    private final UserService userService;

    public Auth(UserService userService) {
        this.userService = userService;
    }

    public String getCurrentUserLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public Long getCurrentUserId(){
        User user = userService.getUserByLogin(getCurrentUserLogin());
        return user.getId();
    }
}
