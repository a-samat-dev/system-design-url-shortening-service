package kz.astana.url_shortener.key_generation_service.repository;

import kz.astana.url_shortener.key_generation_service.model.DeletedKeyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedKeyRepository extends MongoRepository<DeletedKeyEntity, String> {
}
