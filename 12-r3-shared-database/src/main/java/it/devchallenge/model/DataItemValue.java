package it.devchallenge.model;

import java.time.LocalDateTime;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;

import lombok.Data;

@Data
@Document
public class DataItemValue {
    @Field
    private LocalDateTime creationTime;
    @Field
    private String author;
    @Field
    private String value;

    public DataItemValue(String author, String value) {
        this.author = author;
        this.value = value;
        this.creationTime = LocalDateTime.now();
    }
}
