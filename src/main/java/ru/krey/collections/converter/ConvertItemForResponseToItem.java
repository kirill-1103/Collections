package ru.krey.collections.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.krey.collections.interfaces.ItemForResponse;
import ru.krey.collections.model.Item;
import ru.krey.collections.model.User;
import ru.krey.collections.service.ItemService;
import ru.krey.collections.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConvertItemForResponseToItem {

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ConvertItemForResponseToItem(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }


    public List<Item> getItems(List<ItemForResponse> itemsForResponse, Long collectionId){
        List<Item> items = itemService.getItemsByCollectionId(collectionId);
        for(int i = 0;i<items.size();i++){
            Item item = items.get(i);
            ConvertItemForResponse itemForResponse = (ConvertItemForResponse) itemsForResponse.get(i);
            Set<User> usersWhoLikes =  new HashSet<>();
            for(Long id:itemForResponse.getUsersWhoLikesId()){
                User user = userService.getUserById(id);
                if(user!=null){
                    usersWhoLikes.add(user);
                }
            }
            for(User user : usersWhoLikes){
                item.getUsers().add(user);
            }
            item.getUsers().removeIf(user -> !usersWhoLikes.contains(user));
            item.setCountLikes(itemForResponse.getCountLikes());
        }
        return items;
    }
}
