package ru.krey.collections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.krey.collections.model.Role;
import ru.krey.collections.model.User;
import ru.krey.collections.repos.UserRepo;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByLogin(username);
        if(user == null)
            throw new UsernameNotFoundException(username);
        repo.save(user);
        return user;
    }

    private boolean checkUserNotInDb(User user){
        User userFromDb = repo.findByLogin(user.getLogin());
        return userFromDb == null;
    }

    public boolean addUser(User user){
        if(!checkUserNotInDb(user)) return false;
        initAndSaveUser(user,false);
        return true;
    }

    public boolean addAdmin(User user){
        if(!checkUserNotInDb(user)) return false;
        initAndSaveUser(user,true);
        return true;
    }

    private void initAndSaveUser(User user,boolean admin){
        user.setStatus(true);
        user.setRoles(Collections.singleton(admin ? Role.ADMIN : Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
    }
}
