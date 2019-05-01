package it.devchallenge.storage;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class MongoFileStorageService implements FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final MongoDbFactory dbFactory;

    @Override
    @SneakyThrows
    public String upload(MultipartFile file, String userName) {
        String filename = file.getOriginalFilename();
        log.info("Uploading file: {}", filename);
        return gridFsTemplate.store(file.getInputStream(), filename, file.getContentType(),
            new BasicDBObject("user", userName)).toString();
    }

    @Override
    public List<FileHistory> userFiles(String userName) {
        log.info("Searching files for user: {}", userName);
        Query query = new Query(Criteria.where("metadata.user").is(userName));
        return StreamSupport.stream(gridFsTemplate.find(query).spliterator(), false)
            .collect(groupingBy(GridFSFile::getFilename))
            .entrySet().stream()
            .map(this::toFileHistory)
            .collect(toList());
    }

    @Override
    public List<FileSnapshot> fileVersions(String fileName, String userName) {
        Query query = new Query(Criteria.where("filename").is(fileName).and("metadata.user").is(userName));
        return StreamSupport.stream(gridFsTemplate.find(query).spliterator(), false)
            .map(this::toFileSnapshot)
            .collect(toList());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ResponseEntity<Resource> downloadByFileId(String fileId, String userName) {
        Query query = new Query(Criteria.where("_id").is(fileId).and("metadata.user").is(userName));
        return Optional.ofNullable(gridFsTemplate.findOne(query))
            .map(one ->
                ResponseEntity.ok()
                    .contentLength(one.getLength())
                    .contentType(MediaType.parseMediaType(getContentType(one)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + one.getFilename())
                    .<Resource>body(new InputStreamResource(getGridFs().openDownloadStream(one.getId()))))
            .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @Override
    public ResponseEntity<Resource> downloadLatestByName(String fileName, String userName) {
        return fileVersions(fileName, userName).stream()
            .max(comparing(FileSnapshot::getUploadDate))
            .map(FileSnapshot::getId)
            .map(latestFileId -> downloadByFileId(latestFileId, userName))
            .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    private String getContentType(GridFSFile one) {
        return (String) one.getMetadata().get("_contentType");
    }

    private FileHistory toFileHistory(Map.Entry<String, List<GridFSFile>> entry) {
        List<FileSnapshot> versions = entry.getValue().stream()
            .map(this::toFileSnapshot)
            .collect(toList());
        return new FileHistory(entry.getKey(), versions);
    }

    private FileSnapshot toFileSnapshot(GridFSFile file) {
        return new FileSnapshot(file.getId().asObjectId().getValue().toString(),
            file.getLength(), file.getUploadDate(), file.getMD5(), getContentType(file));
    }

    private GridFSBucket getGridFs() {
        MongoDatabase db = dbFactory.getDb();
        return GridFSBuckets.create(db);
    }
}
