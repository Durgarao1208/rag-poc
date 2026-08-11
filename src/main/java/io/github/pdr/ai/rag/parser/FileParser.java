package io.github.pdr.ai.rag.parser;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileParser {
    List<Document> parse(MultipartFile file);
}
