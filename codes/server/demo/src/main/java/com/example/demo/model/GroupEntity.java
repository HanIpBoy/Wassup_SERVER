package com.example.demo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class GroupEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    private String orginKey;
    private String groupName;
    private String description;
    private int numOfUsers;
    private String leaderId;
    private String lastModifiedAt;
    private String createdAt;

}
