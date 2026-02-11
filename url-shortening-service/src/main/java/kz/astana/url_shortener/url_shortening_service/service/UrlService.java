package kz.astana.url_shortener.url_shortening_service.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kz.astana.url_shortener.key_generation_service.grpc.GetOneKeyRequest;
import kz.astana.url_shortener.key_generation_service.grpc.GetOneKeyResponse;
import kz.astana.url_shortener.key_generation_service.grpc.KeyGenerationGrpcServiceGrpc;
import kz.astana.url_shortener.url_shortening_service.model.dto.ShortenedUrlDTO;
import kz.astana.url_shortener.url_shortening_service.model.dto.UserDTO;
import kz.astana.url_shortener.url_shortening_service.model.entity.ShortenedUrlEntity;
import kz.astana.url_shortener.url_shortening_service.repository.ShortenedUrlRepository;
import kz.astana.url_shortener.url_shortening_service.repository.UrlAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final ShortenedUrlRepository shortenedUrlRepository;
    private final UrlAnalyticsRepository urlAnalyticsRepository;
    private final UserService userService;

    @Value("${key-generation-service.host:localhost}")
    private String keyGenerationHost;

    @Value("${key-generation-service.port:8080}")
    private int keyGenerationPort;

    @Value("${url.expire.years:2}")
    private int expireYears;

    public ShortenedUrlDTO create(ShortenedUrlDTO shortenedUrlDTO, String email, String password) {
        UserDTO userDTO = userService.login(UserDTO.builder().email(email).password(password).build());
        String shortKey = requestShortKey();
        Instant expireAt = LocalDateTime.now()
                .plusYears(expireYears)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        ShortenedUrlEntity entity = ShortenedUrlEntity.builder()
                .userId(userDTO.getId())
                .originalUrl(shortenedUrlDTO.getOriginalUrl())
                .shortenedUrl(shortKey)
                .expireDate(expireAt)
                .creationDate(Instant.now())
                .build();
        shortenedUrlRepository.save(entity);
        shortenedUrlDTO.setShortenedUrl(shortKey);
        shortenedUrlDTO.setExpireDate(entity.getExpireDate());
        shortenedUrlDTO.setCreationDate(entity.getCreationDate());

        return shortenedUrlDTO;
    }

    private String requestShortKey() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(keyGenerationHost, keyGenerationPort)
                .usePlaintext()
                .build();
        try {
            KeyGenerationGrpcServiceGrpc.KeyGenerationGrpcServiceBlockingStub stub =
                    KeyGenerationGrpcServiceGrpc.newBlockingStub(channel);
            GetOneKeyResponse response = stub.getOneKey(GetOneKeyRequest.newBuilder().build());
            return response.getKey();
        } finally {
            channel.shutdown();
        }
    }

    public void redirect(String shortUrl) {

    }
}
