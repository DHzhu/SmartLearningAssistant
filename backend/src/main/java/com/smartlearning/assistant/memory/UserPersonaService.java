package com.smartlearning.assistant.memory;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPersonaService {

    private final UserMemoryRepository memoryRepository;
    private final MemoryExtractionService extractionService;

    public UserPersonaService(
            UserMemoryRepository memoryRepository,
            MemoryExtractionService extractionService) {
        this.memoryRepository = memoryRepository;
        this.extractionService = extractionService;
    }

    public List<UserMemory> getUserMemories(Long userId) {
        return memoryRepository.findByUserIdOrderByImportanceDescUpdatedAtDesc(userId);
    }

    @Transactional
    public UserMemory saveMemory(Long userId, String category, String content, int importance) {
        UserMemory memory = new UserMemory(userId, category, content, importance);
        return memoryRepository.save(memory);
    }

    @Transactional
    public void deleteMemory(Long id, Long userId) {
        memoryRepository.deleteByIdAndUserId(id, userId);
    }

    @Transactional
    public void extractAndPersistMemories(Long userId, String userMessage) {
        List<MemoryExtractionService.ExtractedFact> facts = extractionService.extractLearnerFacts(userMessage);
        for (MemoryExtractionService.ExtractedFact fact : facts) {
            saveMemory(userId, fact.category(), fact.content(), fact.importance());
        }
    }

    public String buildLearnerProfilePrompt(Long userId) {
        List<UserMemory> memories = memoryRepository.findByUserIdOrderByImportanceDescUpdatedAtDesc(userId);
        if (memories.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n【学生个性化档案与历史薄弱点】\n");
        int count = 0;
        for (UserMemory m : memories) {
            sb.append("• [").append(m.getCategory()).append("] ").append(m.getContent()).append("\n");
            count++;
            if (count >= 5) break; // limit to top 5 most important facts to preserve context window
        }

        return sb.toString();
    }
}
