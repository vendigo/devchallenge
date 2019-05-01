package it.devchallenge.storage;

import java.util.Date;

import lombok.Value;

@Value
public class FileSnapshot {
    String id;
    long length;
    Date uploadDate;
    String md5;
    String contentType;
}
