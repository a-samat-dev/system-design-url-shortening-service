package kz.astana.url_shortener.key_generation_service.service;

import jakarta.annotation.PostConstruct;
import kz.astana.url_shortener.key_generation_service.model.DeletedKeyEntity;
import kz.astana.url_shortener.key_generation_service.model.KeyEntity;
import kz.astana.url_shortener.key_generation_service.repository.DeletedKeyRepository;
import kz.astana.url_shortener.key_generation_service.repository.KeyRepository;
import kz.astana.url_shortener.key_generation_service.util.Base64KeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyGenerationService {

    private static final String REDIS_KEYS_LIST = "available_keys";
    private static final String REDIS_REFILL_LOCK = "available_keys_refill_lock";
    private static final int REFILL_LOCK_TTL_SECONDS = 5;

    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end",
            Long.class
    );

    private final KeyRepository keyRepository;
    private final DeletedKeyRepository deletedKeyRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${number-of-keys}")
    private long totalNumberOfKeys;

    @Value("${number-of-keys-in-batch}")
    private int numberOfKeysInBatch;

    @Value("${number-of-keys-to-cache}")
    private int numberOfKeysToCache;

    @PostConstruct
    public void generateKeys() {
        Optional<KeyEntity> optionalKeyEntity = keyRepository.findFirstByKeyIsNotNull();

        if (optionalKeyEntity.isEmpty()) {
            log.info("Starting key generation...");
            List<KeyEntity> batchOfKeys = new ArrayList<>(numberOfKeysInBatch);

            for (int i = 0; i < totalNumberOfKeys; i++) {
                KeyEntity keyEntity = new KeyEntity();
                keyEntity.setKey(Base64KeyGenerator.toBase64Key6(i));
                keyEntity.setCreatedAt(Instant.now());
                batchOfKeys.add(keyEntity);

                if (batchOfKeys.size() == numberOfKeysInBatch) {
                    keyRepository.saveAll(batchOfKeys);
                    batchOfKeys.clear();
                }
            }
            log.info("Key generation completed.");
        }
    }

    @Transactional
    public String getOneKey() {
        log.info("Requesting a key from cache...");
        ListOperations<String, String> listOps = stringRedisTemplate.opsForList();

        for (int attempt = 0; attempt < 3; attempt++) {
            String key = listOps.leftPop(REDIS_KEYS_LIST);
            if (key != null) {
                return key;
            }

            log.info("Cache is empty, attempting to refill... (attempt {})", attempt + 1);
            String lockValue = tryAcquireRefillLock();
            if (lockValue != null) {
                try {
                    log.info("Refill lock acquired, refilling cache from MongoDB...");
                    refillCacheFromMongo(listOps);
                } finally {
                    log.info("Releasing refill lock...");
                    releaseRefillLock(lockValue);
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        throw new IllegalStateException("No keys available");
    }

    private String tryAcquireRefillLock() {
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        String lockValue = UUID.randomUUID().toString();
        Boolean acquired = valueOps.setIfAbsent(
                REDIS_REFILL_LOCK,
                lockValue,
                Duration.ofSeconds(REFILL_LOCK_TTL_SECONDS)
        );
        return Boolean.TRUE.equals(acquired) ? lockValue : null;
    }

    private void releaseRefillLock(String lockValue) {
        stringRedisTemplate.execute(
                RELEASE_LOCK_SCRIPT,
                List.of(REDIS_REFILL_LOCK),
                lockValue
        );
    }

    private void refillCacheFromMongo(ListOperations<String, String> listOps) {
        List<KeyEntity> keys = keyRepository.findAll(PageRequest.of(0, numberOfKeysToCache))
                .getContent();

        if (keys.isEmpty()) {
            return;
        }

        List<String> keyValues = keys.stream()
                .map(KeyEntity::getKey)
                .toList();
        List<DeletedKeyEntity> deletedKeys = keys.stream()
                .map(keyEntity -> DeletedKeyEntity.builder()
                        .key(keyEntity.getKey())
                        .deletedAt(Instant.now())
                        .build())
                .toList();
        deletedKeyRepository.saveAll(deletedKeys);
        keyRepository.deleteAll(keys);
        listOps.rightPushAll(REDIS_KEYS_LIST, keyValues);
    }
}
