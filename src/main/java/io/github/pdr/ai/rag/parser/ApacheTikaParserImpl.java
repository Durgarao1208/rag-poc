package io.github.pdr.ai.rag.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApacheTikaParserImpl implements FileParser {
    @Override
    public List<Document> parse(MultipartFile file) {
        Metadata metadata = new Metadata();

        String content;

        try (InputStream is = file.getInputStream()) {

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
            documentMetadata.put("detectedType", metadata.get(Metadata.CONTENT_TYPE));
            documentMetadata.put("ingestedAt", Instant.now().toString());

            Document document = new Document(content, documentMetadata);
            return List.of(document);
        } catch (Exception ex) {
            log.error("Failed to parse file {}", file.getOriginalFilename(), ex);

            throw new RuntimeException(
                    "Failed to parse file: " + file.getOriginalFilename(),
                    ex
            );
        }
    }
}
