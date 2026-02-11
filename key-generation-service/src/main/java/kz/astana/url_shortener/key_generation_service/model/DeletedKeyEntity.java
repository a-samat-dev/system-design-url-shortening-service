package kz.astana.url_shortener.key_generation_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "deleted_keys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedKeyEntity {

    @Id
    private String key;

    private Instant deletedAt;
}
