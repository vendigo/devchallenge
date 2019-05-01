package it.devchallenge;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;

import it.devchallenge.document.FilesProcessor;

public class TestFileParserRunner implements CommandLineRunner {
    @Autowired
    private FilesProcessor filesProcessor;
    @Value("classpath:testFile.pdf")
    private Resource testFile;
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Override
    public void run(String... strings) throws Exception {
        tempFolder.create();
        File testFolder = tempFolder.newFolder("testFolder");
        Path outPath = Paths.get(testFolder.getPath(), "testFile.pdf");
        try (InputStream fileStream = testFile.getInputStream()) {
            Files.copy(fileStream, outPath);
        }
        filesProcessor.processFiles(testFolder.getPath());
        testFolder.delete();
    }
}
