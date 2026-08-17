package com.smartlearning.assistant.knowledge.chunking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SemanticChunker {

    /**
     * Chunks text along semantic boundaries (paragraphs and sentences) up to targetChunkSize.
     */
    public List<String> chunkText(String text, int targetChunkSize, int overlap) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalized = text.replace("\r\n", "\n");
        String[] paragraphs = normalized.split("\n\n+");

        List<String> rawUnits = new ArrayList<>();
        for (String para : paragraphs) {
            String trimmed = para.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.length() <= targetChunkSize) {
                rawUnits.add(trimmed);
            } else {
                // Split on sentence boundaries
                String[] sentences = trimmed.split("(?<=[。！？!?.;；\n])");
                StringBuilder current = new StringBuilder();
                for (String sentence : sentences) {
                    if (current.length() + sentence.length() > targetChunkSize && current.length() > 0) {
                        rawUnits.add(current.toString().trim());
                        current = new StringBuilder();
                    }
                    current.append(sentence);
                }
                if (current.length() > 0) {
                    rawUnits.add(current.toString().trim());
                }
            }
        }

        // Assemble raw units into final chunks respecting targetChunkSize
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String unit : rawUnits) {
            if (currentChunk.length() + unit.length() + 2 > targetChunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            if (currentChunk.length() > 0) {
                currentChunk.append("\n\n");
            }
            currentChunk.append(unit);
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }
}
