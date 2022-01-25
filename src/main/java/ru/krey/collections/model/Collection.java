package ru.krey.collections.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty(message = "Name can't be empty!")
    String name;

    @NotNull
    @NotEmpty(message = "Theme can't be ampty!")
    String theme;

    String image;

    @ManyToOne
    User creator;

    @NotNull
    @NotEmpty(message = "Description can't be empty!")
    String description;

    Integer countItems;

    String num1, num2, num3;

    String bool1, bool2, bool3;

    String date1, date2, date3;

    String str1, str2, str3;

    String text1,text2,text3;
}