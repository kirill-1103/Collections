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

    Long num1, num2, num3;

    Boolean bool1, bool2, bool3;

    Date date1, date2, date3;

    String str1, str2, str3;

    String text1,text2,text3;

    Integer countLikes;
}
