package it.devchallenge.hashphone.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class FileUtils {

    @SneakyThrows
    public static String readSystemResource(final String location) {
        return readResource(Paths.get(ClassLoader.getSystemResource(location).toURI()));
    }

    @SneakyThrows
    public static String readSystemResource(final String location, final ClassLoader classLoader) {
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(location)) {
            return StreamUtils.copyToString(resourceAsStream, Charsets.UTF_8);
        }
    }

    public static String readResource(final String location) {
        return readResource(Paths.get(location));
    }

    @SneakyThrows
    static String readResource(final Path path) {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Nullable
    public static String toUnixStyleLineSeparators(@Nullable String fileContent) {
        if (fileContent == null) {
            return null;
        }
        return fileContent
            .replaceAll(StringUtils.CR + StringUtils.LF, StringUtils.LF)
            .replaceAll(StringUtils.CR, StringUtils.LF);
    }
}
