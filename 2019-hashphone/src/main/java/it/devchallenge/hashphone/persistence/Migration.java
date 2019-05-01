package it.devchallenge.hashphone.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Migration {

    @Id
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String algorithm;
    private String salt;

    public Migration(LocalDateTime startDate, LocalDateTime endDate, String algorithm, String salt) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.algorithm = algorithm;
        this.salt = salt;
    }
}
