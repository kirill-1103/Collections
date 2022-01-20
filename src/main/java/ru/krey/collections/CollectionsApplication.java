package ru.krey.collections;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.krey.collections.model.User;
import ru.krey.collections.service.UserService;

@SpringBootApplication
public class CollectionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectionsApplication.class, args);
    }
}

