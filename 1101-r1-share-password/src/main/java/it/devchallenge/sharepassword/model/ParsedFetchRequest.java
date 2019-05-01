package it.devchallenge.sharepassword.model;

import lombok.Value;

@Value
public class ParsedFetchRequest {
    long passwordId;
    String verificationCode;
    String secretKey;
}
