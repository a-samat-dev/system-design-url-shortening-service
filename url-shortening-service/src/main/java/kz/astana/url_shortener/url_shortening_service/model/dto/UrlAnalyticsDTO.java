package kz.astana.url_shortener.url_shortening_service.model.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
public class UrlAnalyticsDTO {

    private String shortenedUrlId;
    private Instant dateTimeClicked;
    private String referringSite;
}
