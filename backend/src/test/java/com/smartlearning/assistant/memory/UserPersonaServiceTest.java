package com.smartlearning.assistant.memory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPersonaServiceTest {

    @Mock
    private UserMemoryRepository memoryRepository;

    @Mock
    private MemoryExtractionService extractionService;

    private UserPersonaService personaService;

    @BeforeEach
    void setUp() {
        personaService = new UserPersonaService(memoryRepository, extractionService);
    }

    @Test
    void shouldFormatLearnerProfilePrompt() {
        UserMemory m1 = new UserMemory(1L, "GOAL", "备考英语六级 (目标 550+)", 5);
        UserMemory m2 = new UserMemory(1L, "WEAKNESS", "听力长对话和听写薄弱", 4);
        when(memoryRepository.findByUserIdOrderByImportanceDescUpdatedAtDesc(1L)).thenReturn(List.of(m1, m2));

        String prompt = personaService.buildLearnerProfilePrompt(1L);

        assertNotNull(prompt);
        assertTrue(prompt.contains("备考英语六级"));
        assertTrue(prompt.contains("听力长对话"));
    }

    @Test
    void shouldReturnEmptyPromptWhenNoMemory() {
        when(memoryRepository.findByUserIdOrderByImportanceDescUpdatedAtDesc(2L)).thenReturn(List.of());

        String prompt = personaService.buildLearnerProfilePrompt(2L);

        assertEquals("", prompt);
    }
}
