package kz.astana.url_shortener.url_shortening_service.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "shortenedUrls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortenedUrlEntity {

    @Id
    private String id;
    private String userId;
    @Indexed(unique = true)
    private String originalUrl;
    @Indexed(unique = true)
    private String shortenedUrl;
    private Instant expireDate;
    private Instant creationDate;
}
