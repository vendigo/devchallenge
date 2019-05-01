package it.devchallenge.document;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.devchallenge.api.repository.ProjectRepository;
import it.devchallenge.document.domain.VoteSession;
import it.devchallenge.graph.VoteSessionConverter;
import it.devchallenge.graph.domain.Project;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilesProcessor {
    private static final String PDF_EXTENSION = ".pdf";
    public static final int MAX_DEPTH = 1;
    @Autowired
    private SessionParser sessionParser;
    @Autowired
    private VoteSessionConverter converter;
    @Autowired
    private ProjectRepository projectRepository;

    @SneakyThrows
    public void processFiles(String inputFolder) {
        Path inputFolderPath = Paths.get(inputFolder);
        List<Project> projects = Files.walk(inputFolderPath, MAX_DEPTH)
                .parallel()
                .filter(path -> path.toFile().getName().endsWith(PDF_EXTENSION))
                .flatMap(this::processFile)
                .map(converter::convert)
                .collect(toList());
        projectRepository.save(projects);
    }

    @SneakyThrows
    private Stream<VoteSession> processFile(Path path) {
        log.info("Processing file: {}", path.toFile().getName());
        try (PDDocument document = PDDocument.load(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return sessionParser.parseSessions(text);
        }
    }
}
