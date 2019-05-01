package it.devchallenge.sharepassword.service;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import it.devchallenge.sharepassword.model.ParsedFetchRequest;

@Service
public class LinkConverterService {
    private static final String DOT_REGEX = "\\.";
    private static final String DOT = ".";

    ParsedFetchRequest parseLink(String linkBody) {
        String decodedLink = new String(Base64.getDecoder().decode(linkBody));
        String[] parts = decodedLink.split(DOT_REGEX);
        isTrue(parts.length == 3, "Decoded message should contain 3 parts delimited by dot");
        long passwordId = Long.parseLong(parts[0]);
        String verificationCode = notBlank(parts[1]);
        String secretKey = notBlank(parts[2]);
        return new ParsedFetchRequest(passwordId, verificationCode, secretKey);
    }

    String buildLink(Long id, String verificationCode, String secretKey) {
        String rawLink = Stream.of(String.valueOf(id), verificationCode, secretKey)
                .collect(Collectors.joining(DOT));
        return Base64.getEncoder().encodeToString(rawLink.getBytes());
    }
}
