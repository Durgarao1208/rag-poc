package io.github.pdr.ai.rag.service;

import io.github.pdr.ai.rag.parser.FileParser;
import io.github.pdr.ai.rag.splitter.ChunkingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionServiceImpl implements IngestionService{

    private final FileParser apacheTikaParser;
    private final ChunkingService chunkingService;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    @Override
    public void ingest(MultipartFile file) {
        log.info("Started ingestion for file: {}", file.getOriginalFilename());

        List<Document> document = apacheTikaParser.parse(file);
        List<Document> chunks = chunkingService.chunk(document);

        chunks.forEach(this::embedingModelCheck);

        // Step 4: Store
//        vectorStore.add(chunks);

        log.info("Ingested file={} chunks={}",file.getOriginalFilename(),chunks.size());
    }

    private void enrichMetadata(
            List<Document> chunks,
            MultipartFile file) {

        int chunkNumber = 1;

        for (Document chunk : chunks) {

            chunk.getMetadata().put(
                    "fileName",
                    Objects.requireNonNull(file.getOriginalFilename())
            );

            chunk.getMetadata().put(
                    "contentType",
                    Objects.requireNonNull(file.getContentType())
            );

            chunk.getMetadata().put(
                    "chunkNumber",
                    chunkNumber++
            );
        }
    }

    private void embedingModelCheck(Document document) {
        if (document.getText() == null) return;

        log.info("chunk size {}", document.getText().length());
//        log.info("---------------------chunk content begin ---------------/n");
//        log.info(document.getText());
//        log.info("---------------------chunk content end ---------------/n");


        long start = System.currentTimeMillis();
        embeddingModel.embed(document.getText());

        log.info("{} embedding took {} ms", document.getMetadata().get("chunk_index"), (System.currentTimeMillis() - start));
    }
}
