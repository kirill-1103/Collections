package ru.krey.collections.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krey.collections.model.User;

public interface ItemRepo extends JpaRepository<User,Long> {

}
