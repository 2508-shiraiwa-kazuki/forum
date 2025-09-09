package com.example.forum.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
//Table名はDBの名称と一致
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @Column(name = "id")
    //自動採番(AUTO_INCREMENT)の付与
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content_id")
    private int contentId;

    @Column(name = "comment")
    private String comment;

}
