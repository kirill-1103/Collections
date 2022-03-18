package ru.krey.collections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.Item;
import ru.krey.collections.repos.ItemRepo;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    ItemRepo itemRepo;

    @Autowired
    CollectionService collectionService;

    public List<Item> getItemsByCollectionId(Long id){
        return itemRepo.findAllByCollectionId(id);
    }

    public void addItem(Item item){
        item.setCountLikes(0);
        itemRepo.save(item);
        item.getCollection().setCountItems(item.getCollection().getCountItems()+1);
        collectionService.updateCollection(item.getCollection());
    }

    public void deleteItem(Item item){
        item.getCollection().setCountItems(item.getCollection().getCountItems()-1);
        collectionService.updateCollection(item.getCollection());
        itemRepo.delete(item);
    }
}
