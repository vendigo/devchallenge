package it.devchallenge.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

import lombok.Data;

@Data
@Document
public class DataItem {
    @Id
    @Field
    private String key;
    @Field
    private List<DataItemValue> values;

    public DataItem(String key) {
        this.key = key;
        this.values = new ArrayList<>();
    }
}
