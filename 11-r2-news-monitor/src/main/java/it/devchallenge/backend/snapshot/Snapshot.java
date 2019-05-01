package it.devchallenge.backend.snapshot;

import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one version of some article
 */
@Data
@Entity
@NoArgsConstructor
public class Snapshot {
    public static final int CONTENT_MAX_LENGTH = 50000;
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private Long articleId;
    @Column
    private LocalDateTime time;
    @Column
    private String content;

    public Snapshot(Long articleId, String content) {
        this.articleId = notNull(articleId);
        this.content = abbreviate(content, CONTENT_MAX_LENGTH);
        this.time = LocalDateTime.now();
    }
}
