package com.exmaple.upload.Domain.MongoDb;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user")
public class MongoDBTestModel {

    private String id;
    private String name;
    private int age;
}
