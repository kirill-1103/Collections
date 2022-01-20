package ru.krey.collections.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.User;

import java.util.List;

public interface CollectionRepo extends JpaRepository<Collection,Long> {
    List<Collection> findAllByCreatorId(Long id);
}
