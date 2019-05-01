package it.devchallenge.hashphone.persistence;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HashedPhone {

    @Id
    private String id;
    private String hashValue;
    private String newHashValue;
    private String phoneNumber;
    private String migrationId;

    public HashedPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashedPhone(String hashValue, String phoneNumber) {
        this.hashValue = hashValue;
        this.newHashValue = hashValue;
        this.phoneNumber = phoneNumber;
    }

    public HashedPhone(String hashValue, String newHashValue, String phoneNumber, String migrationId) {
        this.hashValue = hashValue;
        this.newHashValue = newHashValue;
        this.phoneNumber = phoneNumber;
        this.migrationId = migrationId;
    }
}
