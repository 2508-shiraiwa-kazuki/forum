package com.example.forum.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

//@Entityと@Tableを指定することでpublic class ReportがDBのreportテーブルと関連付けられている
@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {
    // @Idと@Culumn併せてDBのIdフィールドとの関連付け
    @Id
    @Column
    // AUTO_INCREMENTを付与する（自動採番）
    // IDENTITYを指定する場合はDBでIdの型をSERIALに指定する必要がある
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // DBのcontentカラムとの関連付け
    @Column
    private String content;
    // insertable・updatable = 登録・更新時に日時を上書きする・しない設定
    @Column(name = "created_date", insertable = false,updatable = false)
    private Timestamp createdDate;
    @Column(name = "updated_date", insertable = false, updatable = true)
    private Timestamp updatedDate;
}
