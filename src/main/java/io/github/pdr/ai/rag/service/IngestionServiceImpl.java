package io.github.pdr.ai.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionServiceImpl implements IngestionService{

    private final TokenTextSplitter textSplitter;

    @Override
    public void ingest(MultipartFile file) {
        log.info("Started ingestion for file: {}", file.getOriginalFilename());

        Metadata metadata = new Metadata();

        String content;

        try(InputStream is = file.getInputStream()) {

            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            ParseContext context = new ParseContext();

            parser.parse(
                    is,
                    handler,
                    metadata,
                    context
            );

            content = handler.toString();

            if (content == null || content.isBlank()) {
                throw new RuntimeException("No text extracted from file");
            }

            Map<String, Object> documentMetadata = new HashMap<>();
            documentMetadata.put("fileName", file.getOriginalFilename());
            documentMetadata.put("contentType", file.getContentType());
            documentMetadata.put("detectedType",metadata.get(Metadata.CONTENT_TYPE));
            documentMetadata.put("ingestedAt",Instant.now().toString());

            Document document = new Document(content, documentMetadata);
            List<Document> chunks = textSplitter.split(document);

            log.info("Ingested file={} chunks={}",file.getOriginalFilename(),chunks.size());

        } catch (Exception ex) {
            log.error("Failed to ingest document {}", file.getOriginalFilename(), ex);

            throw new RuntimeException(
                    "Failed to ingest document: " + file.getOriginalFilename(),
                    ex
            );
        }
    }
}
