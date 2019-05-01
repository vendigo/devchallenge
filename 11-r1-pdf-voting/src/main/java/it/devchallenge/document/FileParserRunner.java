package it.devchallenge.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("fileParserRunner")
@Slf4j
public class FileParserRunner implements CommandLineRunner {
    @Value("${input.folder}")
    private String inputFolder;
    @Autowired
    private FilesProcessor filesProcessor;

    @Override
    public void run(String... strings) throws Exception {
        log.info("Loading files from folder {}", inputFolder);
        filesProcessor.processFiles(inputFolder);
    }
}
