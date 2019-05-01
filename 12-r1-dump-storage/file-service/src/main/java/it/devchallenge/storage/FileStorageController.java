package it.devchallenge.storage;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.devchallenge.security.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
public class FileStorageController {

    private final FileStorageService fileStorageService;
    private final UserService userService;

    @PostMapping("/")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return fileStorageService.upload(file, userService.getCurrentUserName());
    }

    @GetMapping("/")
    public List<FileHistory> userFiles() {
        return fileStorageService.userFiles(userService.getCurrentUserName());
    }

    @GetMapping("/versions/{fileName}")
    public List<FileSnapshot> fileVersions(@PathVariable("fileName") String fileName) {
        return fileStorageService.fileVersions(fileName, userService.getCurrentUserName());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadById(@PathVariable("fileId") String fileId) {
        return fileStorageService.downloadByFileId(fileId, userService.getCurrentUserName());
    }

    @GetMapping("/download/latest/{fileName}")
    public ResponseEntity<Resource> downloadLatestByName(@PathVariable("fileName") String fileName) {
        return fileStorageService.downloadLatestByName(fileName, userService.getCurrentUserName());
    }
}
