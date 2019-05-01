package it.devchallenge.sharepassword.model;

import static javax.persistence.EnumType.STRING;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "FETCH_HISTORY")
public class FetchHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private Long passwordId;
    @Column
    private LocalDateTime createdDate;
    @Column
    @Enumerated(STRING)
    private FetchStatus fetchStatus;
}
