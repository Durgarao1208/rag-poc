package io.github.pdr.ai.rag.controller;

import io.github.pdr.ai.rag.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final IngestionService ingestionService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file")MultipartFile file
            ) {
        try {
            ingestionService.ingest(file);
            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "fileName", Objects.requireNonNull(file.getOriginalFilename()),
                            "message", "Document indexed successfully"
                    )
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "status", "FAILED",
                                    "fileName", Objects.requireNonNull(file.getOriginalFilename()),
                                    "message", ex.getMessage()
                            )
                    );
        }
    }
}
