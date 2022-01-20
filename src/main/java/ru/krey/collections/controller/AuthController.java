package ru.krey.collections.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.krey.collections.model.User;
import ru.krey.collections.service.UserService;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String getSignup(){
//        tempFillData();
        return "signup";
    }

    @PostMapping("/signup")
    public String createNewUser(@Valid User user, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            addErrors(user,model,bindingResult);
            return "signup";
        }
        if(!userService.addUser(user)){
            model.addAttribute("user_exists",true);
            model.addAttribute("user",user);
            return "signup";
        }
        return "redirect:/login";
    }

    private void addErrors(User user,Model model,BindingResult bindingResult){
        Map<String,String> errors = getErrors(bindingResult);
        model.addAttribute("errors",errors);
        model.addAttribute("user",user);
    }

    private Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    private void tempFillData(){
        userService.addAdmin(new User("admin","123","Администратор","admin@mail.ru",null,null,true));
        userService.addUser(new User("user1","123","Юзер1","u1@mail.ru",null,null,true));
        userService.addUser(new User("user2","123","Юзер2","u2@mail.ru",null,null,true));
    }

}
