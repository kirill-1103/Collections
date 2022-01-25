package ru.krey.collections.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.krey.collections.auth.Auth;
import ru.krey.collections.model.Collection;
import ru.krey.collections.service.CollectionService;
import ru.krey.collections.service.UserService;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@RequestMapping("collection")
public class CollectoinController {

    @Autowired
    CollectionService collectionService;

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

    @PostMapping("/new")
    public String addCollection(@Valid Collection collection, BindingResult bindingResult, Model model, @RequestParam("img") MultipartFile img ) throws IOException {
        if(!bindingResult.hasErrors()) {
            String url = null;
            if (img != null) {
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
