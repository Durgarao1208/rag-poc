package io.github.pdr.ai.rag.splitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;

import java.util.IntSummaryStatistics;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkingServiceImpl implements ChunkingService {

    private final TokenTextSplitter tokenTextSplitter;

    @Override
    public List<Document> chunk(List<Document> documents) {

        log.info("Starting chunking for {} document", documents.size());

        List<Document> chunks =
                tokenTextSplitter.apply(documents);

        log.info("Generated {} chunk(s)", chunks.size());

        IntSummaryStatistics stats = chunks.stream()
                .filter(d -> d.getText() != null)
                .mapToInt(d -> d.getText().length())
                .summaryStatistics();

        log.info("chunk text summary /n {}", stats);

        return chunks;
    }
}
