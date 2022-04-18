package ru.krey.collections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.krey.collections.converter.ConvertItemForResponse;
import ru.krey.collections.interfaces.ItemForResponse;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.Item;
import ru.krey.collections.repos.ItemRepo;

import java.util.*;

@Service
public class ItemService {
    @Autowired
    ItemRepo itemRepo;

    @Autowired
    CollectionService collectionService;

    public List<Item> getItemsByCollectionId(Long id){
        return itemRepo.findAllByCollectionId(id);
    }

    public List<Item> getItemsByNameAllCases(String name){
        Set<Item> itemsSet = new HashSet<>();
        List<Item> itemsList1 = itemRepo.findAllByName(name);
        List<Item> itemsList2 = itemRepo.findAllByName(name.toLowerCase(Locale.ROOT));
        List<Item> itemsList3 = itemRepo.findAllByName(name.toUpperCase(Locale.ROOT));
        itemsSet.addAll(itemsList1);
        itemsSet.addAll(itemsList2);
        itemsSet.addAll(itemsList3);
        return itemsSet.stream().toList();
    }

    public List<Item> getItemsByPartOfName(String part){
        return itemRepo.findAllByNameContainsIgnoreCase(part);
    }

    public List<Item> getItemsByTagsName(String tag){
        return itemRepo.findAllByTags_textIgnoreCase(tag);
    }

    public List<ItemForResponse> getItemsForResponseByCollectionId(Long id){
        List<ItemForResponse> itemsForResponse = new ArrayList<>();
        List<Item> items = itemRepo.findAllByCollectionId(id);
        for(Item item : items){
            itemsForResponse.add(new ConvertItemForResponse(item));
        }
        return itemsForResponse;
    }

    public void addItem(Item item){
        item.setCountLikes(0);
        itemRepo.save(item);
        item.getCollection().setCountItems(item.getCollection().getCountItems()+1);
        collectionService.updateCollection(item.getCollection());
    }

    public void updateItem(Item item){
        itemRepo.save(item);
    }

    public void deleteItem(Item item){
        item.getCollection().setCountItems(item.getCollection().getCountItems()-1);
        collectionService.updateCollection(item.getCollection());
        itemRepo.delete(item);
    }
}
