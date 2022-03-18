package ru.krey.collections.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.krey.collections.auth.Auth;
import ru.krey.collections.model.*;
import ru.krey.collections.model.Collection;
import ru.krey.collections.service.CollectionService;
import ru.krey.collections.service.ItemService;
import ru.krey.collections.service.TagService;
import ru.krey.collections.service.UserService;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@RequestMapping("collection")
public class CollectoinController {

    @Autowired
    CollectionService collectionService;

    @Autowired
    ItemService itemService;

    @Autowired
    TagService tagService;

    @Autowired
    private Cloudinary cloudinaryConfig ;

    @Autowired
    private final Auth auth;

    @Autowired
    private final UserService userService;

    @Value("${upload.path}")
    private String path;

    public CollectoinController(Auth auth, UserService userService) {
        this.auth = auth;
        this.userService = userService;
    }

    @GetMapping("/new")
    public String getCollectionCreating(){
        return "new_collection";
    }

    @GetMapping("/{id}")
    public String getCollection(@PathVariable Long id, Model model){
        model.addAttribute("collection",collectionService.getCollectionById(id));
        model.addAttribute("items",itemService.getItemsByCollectionId(id));
        return "collection";
    }

    @PostMapping("/new")
    public String addCollection(@Valid Collection collection, BindingResult bindingResult, Model model, @RequestParam("img") MultipartFile img ) throws IOException {
        if(!bindingResult.hasErrors()) {
            String url = null;
            if (img != null && !img.isEmpty()) {
                url = uploadFile(img);
                collection.setImage(url);
            }
            collection.setCreator(userService.getUserByLogin(auth.getCurrentUserLogin()));
            collectionService.addCollection(collection);
            return "redirect:/account";
        }else{
            addErrors(collection,model,bindingResult);
            return "new_collection";
        }
    }

    @PostMapping ("/delete")
    public String deleteCollection(@RequestParam Long id){
        collectionService.deleteCollectionById(id);
        return "redirect:/account";
    }

    @PostMapping("/edit")
    public String editCollection(@RequestParam Long id,Collection collection,@RequestParam("img") MultipartFile img){
        Collection collectionForEdit = collectionService.getCollectionById(id);
        String image = collectionForEdit.getImage();
        User creator = collectionForEdit.getCreator();
        collectionForEdit = collection;
        if(img == null  || img.isEmpty()){
            collectionForEdit.setImage(image);
        }else{
                String url = uploadFile(img);
                collectionForEdit.setImage(url);
        }
        collectionForEdit.setCreator(creator);
        collectionService.addCollection(collectionForEdit);
        return "redirect:/account";
    }
    @GetMapping("/edit/{id}")
    public String editCollection(@PathVariable Long id,Model model){
        String currentUserLogin = auth.getCurrentUserLogin();
        User user = userService.getUserByLogin(currentUserLogin);
        Collection collection = collectionService.getCollectionById(id);
        if(collection.getCreator().equals(user) || user.getRoles().contains(Role.ADMIN)){
            model.addAttribute("collection",collection);
            return "edit_collection";
        }else{
            return "redirect:/";
        }
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

    @GetMapping("/items/{id}")
    public ResponseEntity<List<Item>> getItems(@PathVariable Long id){
        return new ResponseEntity<>(itemService.getItemsByCollectionId(id), HttpStatus.OK);
    }


    private void addErrors(Collection collection, Model model, BindingResult bindingResult){
        Map<String,String> errors = getErrors(bindingResult);
        model.addAttribute("errors",errors);
        model.addAttribute("collection",collection);
    }

    private Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }



    public String uploadFile(MultipartFile img) {
        try {
            File uploadedFile = convertMultiPartToFile(img);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.asMap("use_filename","true","unique_filename", "false"));
            uploadedFile.delete();
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(UUID.randomUUID()+"."+file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
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


    @Bean
    public Cloudinary cloudinaryConfig() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnni5ursf",
                "api_key", "368329642367983",
                "api_secret", "WEqRnQu2n1fiou2KsDmLyJlP318"));
    }


}
