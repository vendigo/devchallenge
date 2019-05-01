package it.devchallenge.backend.article;

import java.util.List;

import javax.persistence.*;

import it.devchallenge.backend.snapshot.Snapshot;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one article (or document) and all it versions.
 */
@Data
@Entity
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String url;
    @OneToMany(mappedBy = "articleId", fetch = FetchType.LAZY)
    private List<Snapshot> snapshots;

    public Article(String url) {
        this.url = url;
    }
}
