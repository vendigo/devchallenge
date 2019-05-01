package it.devchallenge.storage;

import java.util.List;

import lombok.Value;

@Value
public class FileHistory {
    String fileName;
    List<FileSnapshot> versions;
}
