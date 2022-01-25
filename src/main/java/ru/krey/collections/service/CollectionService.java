package ru.krey.collections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.krey.collections.model.Collection;
import ru.krey.collections.repos.CollectionRepo;

@Service
public class CollectionService {
    @Autowired
    CollectionRepo collectionRepo;

    public void addCollection(Collection collection){
        collection.setCountItems(0);
        collectionRepo.save(collection);
    }
}
