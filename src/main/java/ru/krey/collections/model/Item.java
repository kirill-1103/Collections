package ru.krey.collections.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    Collection collection;

    @NotEmpty
    @NotNull
    String name;

    String text1,text2,text3;

    Integer countLikes;
}
