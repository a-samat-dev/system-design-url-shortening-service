package kz.astana.url_shortener.key_generation_service.repository;

import kz.astana.url_shortener.key_generation_service.model.KeyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyRepository extends MongoRepository<KeyEntity, String> {

    Optional<KeyEntity> findFirstByKeyIsNotNull();
}
