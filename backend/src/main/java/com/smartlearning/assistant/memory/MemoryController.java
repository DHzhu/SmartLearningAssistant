package com.smartlearning.assistant.memory;

import com.smartlearning.assistant.auth.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private final UserPersonaService personaService;

    public MemoryController(UserPersonaService personaService) {
        this.personaService = personaService;
    }

    public record MemoryCreateRequest(
            @NotBlank(message = "Category cannot be blank") String category,
            @NotBlank(message = "Content cannot be blank") String content,
            int importance) {}

    @GetMapping
    public ResponseEntity<List<UserMemory>> getMemories(@AuthenticationPrincipal UserPrincipal user) {
        List<UserMemory> list = personaService.getUserMemories(user.userId());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<UserMemory> addMemory(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody MemoryCreateRequest req) {
        UserMemory created = personaService.saveMemory(
                user.userId(), req.category(), req.content(), req.importance() > 0 ? req.importance() : 3);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMemory(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        personaService.deleteMemory(id, user.userId());
        return ResponseEntity.ok(Map.of("message", "Memory deleted"));
    }
}
