package kz.astana.url_shortener.url_shortening_service.repository;

import kz.astana.url_shortener.url_shortening_service.model.entity.UrlAnalyticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlAnalyticsRepository extends MongoRepository<UrlAnalyticsEntity, String> {
}
