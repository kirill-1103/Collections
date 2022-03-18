package ru.krey.collections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import ru.krey.collections.model.Tag;
import ru.krey.collections.repos.TagRepo;

import java.util.List;
import java.util.Set;

@Service
public class TagService {
    @Autowired
    TagRepo repo;

    public void addTag(Tag tag){
        tag.setCount(0);
        repo.save(tag);
    }

    public Tag findTagById(Long id){
        return repo.findById(id).orElse(null);
    }

    public Tag findTagByText(String text){
        List<Tag> tags = repo.findAllByText(text);
        return tags==null || tags.size() == 0 ? null : tags.get(0);
    }
}
