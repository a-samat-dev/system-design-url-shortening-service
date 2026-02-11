package kz.astana.url_shortener.url_shortening_service.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlAnalyticsEntity {

    @Id
    private String id;
    private String shortenedUrlId;
    private Instant dateTimeClicked;
    private String referringSite;
}
