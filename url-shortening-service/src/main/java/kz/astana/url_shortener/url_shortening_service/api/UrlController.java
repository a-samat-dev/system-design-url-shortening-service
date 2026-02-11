package kz.astana.url_shortener.url_shortening_service.api;

import kz.astana.url_shortener.url_shortening_service.model.dto.ShortenedUrlDTO;
import kz.astana.url_shortener.url_shortening_service.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ShortenedUrlDTO create(@RequestBody ShortenedUrlDTO shortenedUrlDTO,
                                  @RequestHeader String email,
                                  @RequestHeader String password) {
        return urlService.create(shortenedUrlDTO, email, password);
    }

    @GetMapping("/{shortUrl}/redirect")
    public void redirect(@PathVariable String shortUrl) {
        urlService.redirect(shortUrl);
    }
}
