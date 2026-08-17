package com.smartlearning.assistant.chat.rerank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
public class RerankerService {

    private record ScoredDocument(Document document, double score) {}

    /**
     * Re-ranks a list of candidate Documents against a query using hybrid lexical and positional relevance.
     */
    public List<Document> rerank(String query, List<Document> candidates, int topK) {
        if (candidates == null || candidates.isEmpty() || query == null || query.isBlank()) {
            return List.of();
        }

        String[] queryTerms = query.toLowerCase().split("\\s+");
        List<ScoredDocument> scoredDocs = new ArrayList<>();

        for (Document doc : candidates) {
            String text = doc.getText().toLowerCase();
            double score = 0.0;

            // 1. Exact phrase bonus
            if (text.contains(query.toLowerCase())) {
                score += 10.0;
            }

            // 2. Individual keyword matching
            for (String term : queryTerms) {
                if (term.length() < 2) continue;
                int count = 0;
                int idx = 0;
                while ((idx = text.indexOf(term, idx)) != -1) {
                    count++;
                    idx += term.length();
                }
                score += count * 2.0;
            }

            // 3. Early position match bonus
            for (String term : queryTerms) {
                int firstIdx = text.indexOf(term);
                if (firstIdx != -1 && firstIdx < 100) {
                    score += 1.5;
                }
            }

            scoredDocs.add(new ScoredDocument(doc, score));
        }

        // Sort descending by score
        scoredDocs.sort(Comparator.comparingDouble(ScoredDocument::score).reversed());

        int limit = Math.min(Math.max(1, topK), scoredDocs.size());
        List<Document> reranked = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            reranked.add(scoredDocs.get(i).document());
        }

        return reranked;
    }
}
