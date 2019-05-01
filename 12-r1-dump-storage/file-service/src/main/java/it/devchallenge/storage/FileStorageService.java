package it.devchallenge.storage;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String upload(MultipartFile file, String userName);

    List<FileHistory> userFiles(String userName);

    List<FileSnapshot> fileVersions(String fileName, String userName);

    ResponseEntity<Resource> downloadByFileId(String fileId, String userName);

    ResponseEntity<Resource> downloadLatestByName(String fileName, String userName);
}
