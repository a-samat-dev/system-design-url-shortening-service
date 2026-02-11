package kz.astana.url_shortener.url_shortening_service.model.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ShortenedUrlDTO {

    private String userId;
    private String originalUrl;
    private String shortenedUrl;
    private Instant expireDate;
    private Instant creationDate;
}
