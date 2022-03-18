package ru.krey.collections.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krey.collections.model.Item;
import ru.krey.collections.model.User;

import java.util.List;

public interface ItemRepo extends JpaRepository<Item,Long> {
    List<Item> findAllByCollectionId(Long id);
}
