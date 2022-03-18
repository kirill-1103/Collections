package ru.krey.collections.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krey.collections.model.Tag;

import java.util.ArrayList;
import java.util.Set;

public interface TagRepo extends JpaRepository<Tag,Long> {
    ArrayList<Tag> findAllByText(String name);
}
