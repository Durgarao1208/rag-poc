package io.github.pdr.ai.rag.splitter;
import org.springframework.ai.document.Document;

import java.util.List;

public interface ChunkingService {
    List<Document> chunk(List<Document> documents);
}
