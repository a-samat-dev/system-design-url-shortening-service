package kz.astana.url_shortener.url_shortening_service.repository;

import kz.astana.url_shortener.url_shortening_service.model.entity.ShortenedUrlEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenedUrlRepository extends MongoRepository<ShortenedUrlEntity, String> {
}
