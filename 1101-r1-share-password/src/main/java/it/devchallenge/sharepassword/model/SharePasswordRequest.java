package it.devchallenge.sharepassword.model;

import java.util.List;

import lombok.Value;

@Value
public class SharePasswordRequest {
    List<String> passwords;
    Long expireInMinutes;
}
