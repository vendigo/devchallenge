package it.devchallenge.sharepassword.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "SHARED_PASSWORD")
public class SharedPasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String encryptedPassword;
    @Column
    private String verificationCode;
    @Column
    private LocalDateTime creationDate;
    @Column
    private LocalDateTime expirationDate;
    @Column
    private boolean used;

    public SharedPasswordEntity(String encryptedPassword, String verificationCode, LocalDateTime creationDate,
                                LocalDateTime expirationDate) {
        this.encryptedPassword = encryptedPassword;
        this.verificationCode = verificationCode;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public SharedPasswordEntity() {
    }
}
