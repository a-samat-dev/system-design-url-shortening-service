package kz.astana.url_shortener.url_shortening_service.service;

import kz.astana.url_shortener.url_shortening_service.model.dto.UserDTO;
import kz.astana.url_shortener.url_shortening_service.model.entity.UserEntity;
import kz.astana.url_shortener.url_shortening_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDTO register(UserDTO request) {
        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail().trim())
                .email(request.getEmail() == null ? null : request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdDate(Instant.now())
                .build();

        UserEntity saved = userRepository.save(userEntity);
        return toDto(saved);
    }

    public UserDTO login(UserDTO request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        UserEntity user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

//        if (!passwordEncoder.matches(request.getPassword(), user.getEmail())) {
//            throw new IllegalArgumentException("Invalid username or password");
//        }

        return toDto(user);
    }

    private UserDTO toDto(UserEntity userEntity) {
        UserDTO dto = new UserDTO();
        dto.setEmail(userEntity.getEmail());
        dto.setEmail(userEntity.getEmail());
        dto.setCreatedDate(userEntity.getCreatedDate());
        return dto;
    }
}
