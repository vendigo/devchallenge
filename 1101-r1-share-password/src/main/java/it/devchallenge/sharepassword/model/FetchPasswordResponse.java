package it.devchallenge.sharepassword.model;

import java.util.List;

import lombok.Value;

@Value
public class FetchPasswordResponse {
    List<String> passwords;
    FetchStatus fetchStatus;

    public static FetchPasswordResponse error(FetchStatus status) {
        return new FetchPasswordResponse(null, status);
    }

    public static FetchPasswordResponse success(List<String> passwords) {
        return new FetchPasswordResponse(passwords, FetchStatus.SUCCESS);
    }
}
