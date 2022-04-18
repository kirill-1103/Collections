package ru.krey.collections.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.krey.collections.auth.Auth;
import ru.krey.collections.converter.ConvertItemForResponse;
import ru.krey.collections.converter.ConvertItemForResponseToItem;
import ru.krey.collections.interfaces.ItemForResponse;
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
@Slf4j
public class CollectionController {

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

    @Autowired
    private final ConvertItemForResponseToItem convertItemForResponseToItem;

    @Value("${upload.path}")
    private String path;

    public CollectionController(Auth auth, UserService userService, ConvertItemForResponseToItem convertItemForResponseToItem) {
        this.auth = auth;
        this.userService = userService;
        this.convertItemForResponseToItem = convertItemForResponseToItem;
    }

    @GetMapping("/new")
    public String getCollectionCreating(){
        return "new_collection";
    }

    @GetMapping("/{id}")
    public String getCollection(@PathVariable Long id, Model model){
        model.addAttribute("collection",collectionService.getCollectionById(id));
        model.addAttribute("items",itemService.getItemsByCollectionId(id));
        model.addAttribute("userId",auth.getCurrentUserId());
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


    @Bean
    public Cloudinary cloudinaryConfig() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnni5ursf",
                "api_key", "368329642367983",
                "api_secret", "WEqRnQu2n1fiou2KsDmLyJlP318"));
    }


}
