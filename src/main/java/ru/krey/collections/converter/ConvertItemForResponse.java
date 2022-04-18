package ru.krey.collections.converter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import ru.krey.collections.interfaces.ItemForResponse;
import ru.krey.collections.model.Item;
import ru.krey.collections.model.Tag;
import ru.krey.collections.model.User;
import ru.krey.collections.service.ItemService;
import ru.krey.collections.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ConvertItemForResponse implements ItemForResponse {
    private  Long id;
    private  Set<Long> usersWhoLikesId = new HashSet<>();
    private  Integer countLikes;
    private  Long collectionId;

    public ConvertItemForResponse(Long id, Set<Long> usersWhoLikesId, Integer countLikes, Long collectionId){
        this.id=id;
        this.usersWhoLikesId=usersWhoLikesId;
        this.collectionId=collectionId;
        this.countLikes=countLikes;
    }

    public ConvertItemForResponse(Item item){
        this.id = item.getId();
        for(User user:item.getUsers()){
            this.usersWhoLikesId.add(user.getId());
        }
        this.countLikes= item.getCountLikes();
        this.collectionId=item.getCollection().getId();
    }

}
