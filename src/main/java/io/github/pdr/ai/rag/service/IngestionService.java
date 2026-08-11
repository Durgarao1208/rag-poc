package io.github.pdr.ai.rag.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface IngestionService {
    void ingest(MultipartFile file);
}
