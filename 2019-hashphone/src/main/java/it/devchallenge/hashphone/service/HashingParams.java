package it.devchallenge.hashphone.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashingParams {

    private String algorithm;
    private String salt;
    private String migrationId;

    boolean isMigration() {
        return migrationId != null;
    }
}
