package ru.krey.collections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.User;
import ru.krey.collections.repos.CollectionRepo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CollectionService {
    @Autowired
    CollectionRepo collectionRepo;

    public void addCollection(Collection collection){
        collection.setCountItems(0);
        collectionRepo.save(collection);
    }

    public List<Collection> getCollectionsByCreator(User user){
        return collectionRepo.findAllByCreatorId(user.getId());
    }

    public Collection getCollectionById(Long id){
        Optional<Collection> collection = collectionRepo.findById(id);
        return collection.orElse(null);
    }

    public void deleteCollectionById(Long id){
        collectionRepo.deleteById(id);
    }
}
