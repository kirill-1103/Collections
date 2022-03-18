package ru.krey.collections.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    Collection collection;

    @ManyToMany
    @JoinTable(name="item_tag",
            joinColumns = @JoinColumn(name="item_id"),
            inverseJoinColumns = @JoinColumn(name="tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(name="likes",
                joinColumns = @JoinColumn(name="item_id"),
                inverseJoinColumns = @JoinColumn(name="user_id"))
    private Set<User> users = new HashSet<>();

    @NotEmpty
    @NotNull
    String name;

    String text1,text2,text3;

    Integer countLikes;

    public void addTag(Tag tag){
        this.tags.add(tag);
        tag.getItems().add(this);
        tag.count++;
    }

    public void removeTag(Tag tag){
        this.tags.remove(tag);
        tag.getItems().remove(this);
        tag.count--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
