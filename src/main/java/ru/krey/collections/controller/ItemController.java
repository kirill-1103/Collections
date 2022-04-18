package ru.krey.collections.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.krey.collections.converter.ConvertItemForResponse;
import ru.krey.collections.converter.ConvertItemForResponseToItem;
import ru.krey.collections.interfaces.ItemForResponse;
import ru.krey.collections.model.Collection;
import ru.krey.collections.model.Item;
import ru.krey.collections.model.Tag;
import ru.krey.collections.service.CollectionService;
import ru.krey.collections.service.ItemService;
import ru.krey.collections.service.TagService;

import java.util.*;

@Controller
@RequestMapping("items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    private final ConvertItemForResponseToItem convertItemForResponseToItem;

    private final TagService tagService;

    private final CollectionService collectionService;

    public ItemController(ItemService itemService, ConvertItemForResponseToItem convertItemForResponseToItem, TagService tagService, CollectionService collectionService) {
        this.itemService = itemService;
        this.convertItemForResponseToItem = convertItemForResponseToItem;
        this.tagService = tagService;
        this.collectionService = collectionService;
    }


    @PostMapping("/newItem")
    public String newItem(@RequestParam Long id, String name, String text1, String text2, String text3, String tags){
        List<String> tagsArray = new ArrayList<String>(Arrays.asList(tags.split(" ")));
        Item item = initItem(id,name,text1,text2,text3);
        Set<Tag> tagsSet = getTagsSet(tagsArray);
        tagsSet.forEach(item::addTag);
        itemService.addItem(item);
        return "redirect:/collection/"+id;
    }

    @GetMapping("/{id}")
    public @ResponseBody List<ItemForResponse> getItems(@PathVariable Long id){
        return itemService.getItemsForResponseByCollectionId(id);
    }

    @PostMapping(value="",consumes = {"application/json"})
    public @ResponseBody List<ItemForResponse> updateItems(@RequestBody List<ConvertItemForResponse> itemsForResponse){
        log.info("-------------START-------------------");
        Long collectionId;
        List<ItemForResponse> itemsForResponseReal = new ArrayList<>(itemsForResponse);
        if(itemsForResponse.isEmpty()){
            return itemsForResponseReal;
        }
        collectionId = ((ConvertItemForResponse)itemsForResponse.get(0)).getCollectionId();
        List<Item> items = convertItemForResponseToItem.getItems(itemsForResponseReal,collectionId);
        for(Item item : items){
            itemService.updateItem(item);
        }
        log.info("-------------FINISH--------------------");

        return itemService.getItemsForResponseByCollectionId(collectionId);
    }

    @GetMapping("/search")
    public String search(@RequestParam String search, RedirectAttributes attributes){
        final int maxLength = 20;
        if(search.length()>maxLength){
            search = search.substring(0,maxLength);
        }
        Set<Item> itemsByName = new HashSet<>();
        Set<Item> itemsByPartsOfName = new HashSet<>();
        itemsByName.addAll(itemService.getItemsByNameAllCases(search));        //берем все с подходящим именем

        List<Item> itemsByTag = new ArrayList<>();
        List<String> partsOfSearch = Arrays.stream(search.split(" ")).toList();

        for(String part : partsOfSearch){
            itemsByTag.addAll(itemService.getItemsByTagsName(part));
            if(part.length()>3){
                itemsByPartsOfName.addAll(itemService.getItemsByPartOfName(part));
            }
        }

        List<Item> result = new ArrayList<>(itemsByName);
        itemsByPartsOfName.forEach(item-> {
            if(!result.contains(item)){result.add(item);}
        });
        itemsByTag.forEach(item-> {
            if(!result.contains(item)){result.add(item);}
        });

        for(Item item:itemsByName){
            log.info("name:"+item.getName()+":"+item.getCollection().getName());
        }
        for(Item item:itemsByTag){
            log.info("tag:"+item.getName()+":"+item.getCollection().getName());
        }
        for(Item item:itemsByPartsOfName){
            log.info("parts:"+item.getName()+":"+item.getCollection().getName());
        }
        for(Item item:result){
            log.info("result:"+item.getName()+":"+item.getCollection().getName());
        }

        attributes.addAttribute("searchResult",result);
        return "redirect:/";
    }

    private Set<Tag> getTagsSet(List<String> tagsStr){
        Set<Tag> tags = new HashSet<>();
        tagsStr.forEach(tagText->{
            Tag tag = tagService.findTagByText(tagText);
            if(tag==null){
                tag = new Tag();
                tag.setText(tagText);
                tagService.addTag(tag);
            }
            tags.add(tag);
        });
        return tags;
    }

    private Item initItem(Long id,String name,String text1,String text2,String text3){
        Item item = new Item();
        Collection collection = collectionService.getCollectionById(id);
        item.setName(name);
        item.setCollection(collection);
        if(notEmpty(collection.getText1())){
            item.setText1(text1);
        }
        if(notEmpty(collection.getText2())){
            item.setText2(text2);
        }
        if(notEmpty(collection.getText3())){
            item.setText3(text3);
        }
        return item;
    }


    private boolean notEmpty(String str){
        return str!=null && str.length()!=0;
    }

}
